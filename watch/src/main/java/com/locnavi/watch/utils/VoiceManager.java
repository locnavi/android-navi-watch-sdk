package com.locnavi.watch.utils;

import android.content.Context;
import android.text.TextUtils;

import com.baidu.tts.chainofresponsibility.logger.LoggerProxy;
import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.SynthesizerTool;
import com.baidu.tts.client.TtsMode;
import com.locnavi.location.xunjimap.XJLocationSDK;
import com.locnavi.location.xunjimap.utils.Constants;
import com.locnavi.location.xunjimap.utils.IpsConstants;
import com.locnavi.location.xunjimap.utils.L;
import com.locnavi.location.xunjimap.utils.T;
import com.locnavi.watch.XJMapSDK;
//import com.xunji.xunjimap.XJMapSDK;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by liberty on 2017/7/19.
 */

public class VoiceManager implements SpeechSynthesizerListener {
    private String mSampleDirPath;
    private static String SAMPLE_DIR_NAME = "baiduTTS";
    private static final String SPEECH_FEMALE_MODEL_NAME = "bd_etts_speech_female.dat";
    private static final String SPEECH_MALE_MODEL_NAME = "bd_etts_speech_male.dat";
    private static final String TEXT_MODEL_NAME = "bd_etts_text.dat";
    //    private static final String LICENSE_FILE_NAME = "temp_license";
    private static final String ENGLISH_SPEECH_FEMALE_MODEL_NAME = "bd_etts_speech_female_en.dat";
    private static final String ENGLISH_SPEECH_MALE_MODEL_NAME = "bd_etts_speech_male_en.dat";
    private static final String ENGLISH_TEXT_MODEL_NAME = "bd_etts_text_en.dat";
    Context context;
    private SpeechSynthesizer mSpeechSynthesizer;
    ArrayList<String> speakList = new ArrayList<>();
    private Timer timerSpeak;
    private TimerSpeakTask task;
    private boolean isStartFlag = false;
    private String lastBuilding;
    static long temp = 0;
    static Date curentTime = null;
    private Boolean isPlaying = false;
    private long timeStep;
    private String asseFiletMD5Speech;
    private String asseFiletMD5Text;

    // 定义一个私有构造方法
    private VoiceManager() {

    }

    //定义一个静态私有变量(不初始化，不使用final关键字，使用volatile保证了多线程访问时instance变量的可见性，避免了instance初始化时其他变量属性还没赋值完时，被另外线程调用)
    private static volatile VoiceManager instance;

    //定义一个共有的静态方法，返回该类型实例
    public static VoiceManager getInstance(Context context) {
        // 对象实例化时与否判断（不使用同步代码块，instance不等于null时，直接返回对象，提高运行效率）
        if (instance == null) {
            //同步代码块（对象未初始化时，使用同步代码块，保证多线程访问时对象在第一次创建后，不再重复被创建）
            synchronized (VoiceManager.class) {
                //未初始化，则初始instance变量
                if (instance == null) {
                    instance = new VoiceManager(context);
                    temp = new Date().getTime() - 5000;
                }
            }
        }
        return instance;
    }

    private VoiceManager(Context context) {
        this.context = context;
        timerSpeak = new Timer();
        task = new TimerSpeakTask();
        startNavTimerTask();
        initialEnv();
        initialTts();
    }

    private void startNavTimerTask() {
        timerSpeak.schedule(task, 0, 100);
        isStartFlag = true;
    }

    public void stopSpeaking() {
        if (mSpeechSynthesizer != null) {
            mSpeechSynthesizer.stop();
        }
    }

    private class TimerSpeakTask extends TimerTask {
        @Override
        public void run() {
            if (!speakList.isEmpty()) {
                kdxfTextToVoice(speakList.remove(0));
            }
        }
    }

    //添加到集合进行任务添加播报
    public boolean textToVoice(String s) {
        //判断是否在播放其他的,如果正在播放不添加到播报里面
        if (!isPlaying) {
            //没有忙 ,可以添加到队列里面
            //判断是否已经结束后的时间是否 > 3
            curentTime = new Date();
            timeStep = curentTime.getTime() - temp;
            if (timeStep > IpsConstants.VOICE_TIMER_STEP) {
                speakList.add(s);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public void kdxfTextToVoice(String s) {
        //需要合成的文本text的长度不能超过1024个GBK字节。
        int result = this.mSpeechSynthesizer.speak(s, curentTime + "");
        if (result < 0) {
            toPrint("error,please look up error code in doc or URL:http://yuyin.baidu.com/docs/tts/122 ");
        }
    }


    public void clear() {
        speakList.clear();
    }

    public void destroy() {
        mSpeechSynthesizer.release();
        timerSpeak.cancel();
        isStartFlag = false;
        instance = null;
    }

    //强制播报信息,取消之前的播报信息
    public boolean textToVoice(String s, boolean isForcePlay) {
        //需要合成的文本text的长度不能超过1024个GBK字节。
        if (isForcePlay) {
            clear();
            mSpeechSynthesizer.stop();
            int result = this.mSpeechSynthesizer.speak(s);
            //        int result = this.mSpeechSynthesizer.synthesize();
            if (result < 0) {
                toPrint("error,please look up error code in doc or URL:http://yuyin.baidu.com/docs/tts/122 ");
            }
            return true;
        } else {
            return textToVoice(s);
        }
    }

    public boolean textToVoiceFloor(String s, boolean isForcePlay) {
        if (!TextUtils.isEmpty(s)) {
            s = s.replace("-", "负");
        }
        //需要合成的文本text的长度不能超过1024个GBK字节。
        if (isForcePlay) {
            clear();
            mSpeechSynthesizer.stop();
            int result = this.mSpeechSynthesizer.speak(s);
            //        int result = this.mSpeechSynthesizer.synthesize();
            if (result < 0) {
                toPrint("error,please look up error code in doc or URL:http://yuyin.baidu.com/docs/tts/122 ");
            }
            return true;
        } else {
            return textToVoice(s);
        }
    }

    private void initialTts() {
        this.mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        this.mSpeechSynthesizer.setContext(XJLocationSDK.context);
        this.mSpeechSynthesizer.setSpeechSynthesizerListener(this);
        // 文本模型文件路径 (离线引擎使用)
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, mSampleDirPath + "/"
                + TEXT_MODEL_NAME);
        // 声学模型文件路径 (离线引擎使用)
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, mSampleDirPath + "/"
                + SPEECH_FEMALE_MODEL_NAME);
        if (XJLocationSDK.appIdBD != null && XJLocationSDK.apiKeyBD != null && XJLocationSDK.secretKeyBD != null) {
            this.mSpeechSynthesizer.setAppId(XJLocationSDK.appIdBD);
            this.mSpeechSynthesizer.setAppId(XJLocationSDK.appIdBD);
            this.mSpeechSynthesizer.setApiKey(XJLocationSDK.apiKeyBD, XJLocationSDK.secretKeyBD);
        } else {
//            T.showLong("初始化语音失败001");
            L.e("voice", "初始化语音失败001");
            this.mSpeechSynthesizer.setAppId(Constants.BD_APP_ID);
            this.mSpeechSynthesizer.setApiKey(Constants.BD_API_Key, Constants.BD_SECRET_KEY);
        }

        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, "9");
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0");
        // 设置Mix模式的合成策略
        //MIX_MODE_HIGH_SPEED_SYNTHESIZ
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_HIGH_SPEED_SYNTHESIZE);
        //   this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOCODER_OPTIM_LEVE, SpeechSynthesizer.MIX_MODE_HIGH_SPEED_SYNTHESIZE);

        // 初始化tts
        mSpeechSynthesizer.initTts(TtsMode.MIX);
//        AuthInfo authInfo = mSpeechSynthesizer.auth(TtsMode.MIX);
//        if (authInfo.isSuccess()) {
////            T.showLong("授权成功");
//        } else {
//            T.showLong("语音授权失败");
//        }
        //     加载离线英文资源（提供离线英文合成功能）
        int result =
                mSpeechSynthesizer.loadEnglishModel(mSampleDirPath + "/" + ENGLISH_TEXT_MODEL_NAME, mSampleDirPath
                        + "/" + ENGLISH_SPEECH_FEMALE_MODEL_NAME);
        toPrint("loadEnglishModel result=" + result);
        LoggerProxy.printable(true);
        //打印引擎信息和model基本信息
        printEngineInfo();
    }

    /**
     * 打印引擎so库版本号及基本信息和model文件的基本信息
     */
    private void printEngineInfo() {
        toPrint("EngineVersioin=" + SynthesizerTool.getEngineVersion());
        toPrint("EngineInfo=" + SynthesizerTool.getEngineInfo());
        String textModelInfo = SynthesizerTool.getModelInfo(mSampleDirPath + "/" + TEXT_MODEL_NAME);
        toPrint("textModelInfo=" + textModelInfo);
        String speechModelInfo = SynthesizerTool.getModelInfo(mSampleDirPath + "/" + SPEECH_FEMALE_MODEL_NAME);
        toPrint("speechModelInfo=" + speechModelInfo);
    }

    public void toPrint(String str) {
        L.d("kdxf---", str);
    }

    private void initialEnv() {
        if (mSampleDirPath == null) {
//            String sdcardPath = Environment.getExternalStorageDirectory().toString();
            String sdcardPath = context.getFilesDir().getAbsolutePath();
            L.e("sdcardPath:", sdcardPath);
            SAMPLE_DIR_NAME = "baiduTTS" + XJMapSDK.appIdBD;
            mSampleDirPath = sdcardPath + "/" + SAMPLE_DIR_NAME;
        }


        new Thread(new Runnable() {
            @Override
            public void run() {
                makeDir(mSampleDirPath);
                File file = new File(mSampleDirPath);
                try {
                    String desSpeech = mSampleDirPath + "/" + SPEECH_FEMALE_MODEL_NAME;
                    InputStream speechInputStream = context.getResources().getAssets().open(SPEECH_FEMALE_MODEL_NAME);
                    String asseFiletMD5Speech = getAsseFiletMD5(speechInputStream);
                    L.e("asset SPEECH_FEMALE_MODEL_NAME ==  ", asseFiletMD5Speech);
                    copyFromAssetsToSdcard(false, SPEECH_FEMALE_MODEL_NAME, desSpeech);
                    if (file.exists()) {
                        File desSpeechFile = new File(desSpeech);
                        String fileMD5Speech = getFileMD5(desSpeechFile);
                        for (int i = 0; i < IpsConstants.RETRY_COUNT; i++) {
//                            copyFromAssetsToSdcard(false, SPEECH_FEMALE_MODEL_NAME, desSpeech);
                            if (!fileMD5Speech.equals(asseFiletMD5Speech)) {
                                L.e("asset", "SPEECH_FEMALE_MODEL_NAME 校验失败,正在重新copy " + i + "次数");
                                copyFromAssetsToSdcard(true, SPEECH_FEMALE_MODEL_NAME, desSpeech);
                                fileMD5Speech = getFileMD5(desSpeechFile);
                            } else {
                                L.e("asset", "SPEECH_FEMALE_MODEL_NAME 校验成功");
                                break;
                            }
                        }
                        //4b091f2a3d064a7ee85e0be8058f41e4
                    } else {
                        L.e("desSpeech", "不存在");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    String desText = mSampleDirPath + "/" + TEXT_MODEL_NAME;
                    InputStream textInputStream = context.getResources().getAssets().open(TEXT_MODEL_NAME);
                    String asseFiletMD5Text = getAsseFiletMD5(textInputStream);
                    L.e("asset TEXT_MODEL_NAME ==  ", asseFiletMD5Text);
                    copyFromAssetsToSdcard(false, TEXT_MODEL_NAME, desText);
                    if (file.exists()) {
                        File desTextPath = new File(desText);
                        String fileMD5Speech = getFileMD5(desTextPath);
                        for (int i = 0; i < IpsConstants.RETRY_COUNT; i++) {
//                            copyFromAssetsToSdcard(true, TEXT_MODEL_NAME, desText);
                            if (!fileMD5Speech.equals(asseFiletMD5Text)) {
                                L.e("asset", "TEXT_MODEL_NAME 校验失败,正在重新copy " + i + "次数");
                                copyFromAssetsToSdcard(true, TEXT_MODEL_NAME, desText);
                                fileMD5Speech = getFileMD5(desTextPath);
                            } else {
                                L.e("asset", "TEXT_MODEL_NAME 校验成功");
                                break;
                            }
                        }
                    } else {
                        L.e("desText", "不存在");
                    }
                    //12EB5C4A7385B8C3C230C7787A66A5A3  bd_etts_text.dat
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }).start();
        //  copyFromAssetsToSdcard(false, LICENSE_FILE_NAME, mSampleDirPath + "/" + LICENSE_FILE_NAME);
//        copyFromAssetsToSdcard(false, "english/" + ENGLISH_SPEECH_FEMALE_MODEL_NAME, mSampleDirPath + "/"
//                + ENGLISH_SPEECH_FEMALE_MODEL_NAME);
//        copyFromAssetsToSdcard(false, "english/" + ENGLISH_SPEECH_MALE_MODEL_NAME, mSampleDirPath + "/"
//                + ENGLISH_SPEECH_MALE_MODEL_NAME);
//        copyFromAssetsToSdcard(false, "english/" + ENGLISH_TEXT_MODEL_NAME, mSampleDirPath + "/"
//                + ENGLISH_TEXT_MODEL_NAME);
    }


    /**
     * get file md5
     *
     * @param file
     * @return
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    private static String getFileMD5(File file) throws NoSuchAlgorithmException, IOException {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest;
        FileInputStream in;
        byte buffer[] = new byte[1024];
        int len;
        digest = MessageDigest.getInstance("MD5");
        in = new FileInputStream(file);
        while ((len = in.read(buffer, 0, 1024)) != -1) {
            digest.update(buffer, 0, len);
        }
        in.close();
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }

    private static String getAsseFiletMD5(InputStream speechInputStream) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest;
        InputStream in;
        byte buffer[] = new byte[1024];
        int len;
        digest = MessageDigest.getInstance("MD5");
        in = speechInputStream;
        while ((len = in.read(buffer, 0, 1024)) != -1) {
            digest.update(buffer, 0, len);
        }
        in.close();
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }

    /**
     * 将sample工程需要的资源文件拷贝到SD卡中使用（授权文件为临时授权文件，请注册正式授权）
     *
     * @param isCover 是否覆盖已存在的目标文件
     * @param source
     * @param dest
     */
    private void copyFromAssetsToSdcard(boolean isCover, String source, String dest) {
        File file = new File(dest);
        if (isCover || (!isCover && !file.exists())) {
            InputStream is = null;
            FileOutputStream fos = null;
            try {
                is = context.getResources().getAssets().open(source);
                String path = dest;
                fos = new FileOutputStream(path);
                byte[] buffer = new byte[1024];
                int size = 0;
                while ((size = is.read(buffer, 0, 1024)) >= 0) {
                    fos.write(buffer, 0, size);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void makeDir(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    @Override
    public void onSynthesizeStart(String utteranceId) {
        toPrint("onSynthesizeStart utteranceId=" + utteranceId);
    }

    /**
     * 合成数据和进度的回调接口，分多次回调
     *
     * @param utteranceId
     * @param data        合成的音频数据。该音频数据是采样率为16K，2字节精度，单声道的pcm数据。
     * @param progress    文本按字符划分的进度，比如:你好啊 进度是0-3
     */
    @Override
    public void onSynthesizeDataArrived(String utteranceId, byte[] data, int progress) {
        toPrint("onSynthesizeDataArrived");
    }

    /**
     * 合成正常结束，每句合成正常结束都会回调，如果过程中出错，则回调onError，不再回调此接口
     *
     * @param utteranceId
     */
    @Override
    public void onSynthesizeFinish(String utteranceId) {
        toPrint("onSynthesizeFinish utteranceId=" + utteranceId);
    }

    /**
     * 播放开始，每句播放开始都会回调
     *
     * @param utteranceId
     */
    @Override
    public void onSpeechStart(String utteranceId) {
        isPlaying = true;
        toPrint("onSpeechStart utteranceId=" + utteranceId);
    }

    /**
     * 播放进度回调接口，分多次回调
     *
     * @param utteranceId
     * @param progress    文本按字符划分的进度，比如:你好啊 进度是0-3
     */
    @Override
    public void onSpeechProgressChanged(String utteranceId, int progress) {
        toPrint("onSpeechProgressChanged" + progress);
//        if(progress == ){
//            curentTime = new Date();
//            isPlaying = false;
//            temp = curentTime.getTime();
//        }
    }

    /**
     * 播放正常结束，每句播放正常结束都会回调，如果过程中出错，则回调onError,不再回调此接口
     *
     * @param utteranceId
     */
    @Override
    public void onSpeechFinish(String utteranceId) {
        curentTime = new Date();
        temp = curentTime.getTime();
        isPlaying = false;
        toPrint("onSpeechFinish utteranceId=" + utteranceId);
    }

    @Override
    public void onError(String s, SpeechError speechError) {
        curentTime = new Date();
        isPlaying = false;
        temp = curentTime.getTime();
        toPrint("onSpeechFinish utteranceId=" + speechError.toString());
    }

}
