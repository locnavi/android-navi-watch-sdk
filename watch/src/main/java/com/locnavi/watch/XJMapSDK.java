package com.locnavi.watch;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.locnavi.location.xunjimap.XJLocationSDK;
import com.locnavi.location.xunjimap.utils.IpsUtils;
import com.locnavi.location.xunjimap.utils.MixpanelEvent;
import com.locnavi.watch.model.bean.IpsUserInfo;
import com.locnavi.watch.ui.activity.XJMapActivity;
import com.locnavi.watch.ui.widget.ConfirmDialog;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;

import static com.locnavi.location.xunjimap.utils.MixpanelConstants.LOC_SHARE_JOIN_FROM_COPY;
import static com.locnavi.location.xunjimap.utils.MixpanelConstants.LOC_SHARE_JOIN_FROM_URL;
import static com.locnavi.location.xunjimap.utils.MixpanelConstants.LOC_SHARE_JOIN_TYPE_FRIEND_LOCATION;
import static com.locnavi.location.xunjimap.utils.MixpanelConstants.LOC_SHARE_JOIN_TYPE_LOCATION_REGION;
import static com.locnavi.location.xunjimap.utils.MixpanelConstants.LOC_SHARE_JOIN_TYPE_LOC_SHARE;


/**
 * author:chen
 * time:2017/4/24
 * desc:
 */
public class XJMapSDK extends XJLocationSDK {
    protected static final Object monitor = new Object();
    public static Context context;
    private static Configuration configuration;
    private static ConfirmDialog confirmDialog;
    public static IpsUserInfo ipsUser;

    public static void init(Context context, String appKey) {
        init(new Configuration.Builder(context)
                .appKey(appKey)
                .build());
    }

    public static void init(Configuration c) {
        synchronized (monitor) {
            if (c.context == null) {
                throw new RuntimeException("context is null");
            }
            if (c.appKey == null) {
                throw new RuntimeException("appKey is null");
            }
            context = c.context;
            configuration = c;
            initKDXF();
            initShare();
        }
    }

    private static void initKDXF() {
        StringBuffer param = new StringBuffer();
        param.append(SpeechConstant.APPID + "=5c1b60e1");
        param.append(",");
        param.append(SpeechConstant.ENGINE_MODE + "=" + SpeechConstant.MODE_MSC);
        SpeechUtility.createUtility(context, param.toString());
    }

    private static void initShare() {
        if (configuration.shareToWechatListener == null) {
            return;
        }
        Application app = (Application) context;
        app.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {


            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }


            @Override
            public void onActivityResumed(Activity activity) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(context,
                            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        doClipboard(activity);
                    }
                } else {
                    doClipboard(activity);
                }
            }

            @Override
            public void onActivityPaused(Activity activity) {
                if (confirmDialog != null) {
                    confirmDialog.dismiss();
                    confirmDialog = null;
                }
            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    private static String shareName = "", shareGroupId = "";
    private static void doClipboard(Activity activity) {
        String text = IpsUtils.getFirstClipboard(context);
        if (text.contains("map_id") && app != null && app.getScheme() != null &&
                text.contains(app.getScheme())) {
            String paramsStr = text.substring(text.indexOf(":") + 1, text.length());
            String[] paramsArray = paramsStr.split("&");
            if (paramsArray.length > 0) {
                List<String> params = Arrays.asList(paramsArray);
                Intent intent = new Intent(context, XJMapActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                for (int i = 0; i < params.size(); i++) {
                    String[] paramArray = params.get(i).split("=");
                    if (paramArray.length == 2) {
                        if (paramArray[0].equals(XJMapActivity.REQUEST_ID)) {
                            intent.putExtra(XJMapActivity.REQUEST_ID, paramArray[1]);
                        } else if (paramArray[0].equals(XJMapActivity.REQUEST_SHARE_LOC_GROUP_ID)) {
                            shareGroupId = paramArray[1];
                            intent.putExtra(XJMapActivity.REQUEST_SHARE_LOC_GROUP_ID, shareGroupId);
                        } else if (paramArray[0].equals(XJMapActivity.REQUEST_SHARE_LOC_NAME)) {
                            try {
                                shareName = URLDecoder.decode(paramArray[1], "UTF-8");
                                intent.putExtra(XJMapActivity.REQUEST_SHARE_LOC_NAME, shareName);
                            } catch (UnsupportedEncodingException e) {
                                intent.putExtra(XJMapActivity.REQUEST_SHARE_LOC_NAME, "");
                                e.printStackTrace();
                            }
                        } else if (paramArray[0].equals(XJMapActivity.REQUEST_SHARE_LOC_FLOOR)) {
                            intent.putExtra(XJMapActivity.REQUEST_SHARE_LOC_FLOOR, paramArray[1]);
                        } else if (paramArray[0].equals(XJMapActivity.REQUEST_SHARE_LOC_LAT)) {
                            intent.putExtra(XJMapActivity.REQUEST_SHARE_LOC_LAT, paramArray[1]);
                        } else if (paramArray[0].equals(XJMapActivity.REQUEST_SHARE_LOC_LNG)) {
                            intent.putExtra(XJMapActivity.REQUEST_SHARE_LOC_LNG, paramArray[1]);
                        }
                    }
                }
                confirmDialog = new ConfirmDialog(activity, context.getString(R.string.ipsmap_dilog_content), (dialog, which) -> {
                    if (activity.getClass() == XJMapActivity.class) {
                        activity.finish();
                    }
                    new Handler().postDelayed(() -> {
                        context.startActivity(intent);
                    }, 1000);
                    IpsUtils.clearClipboard(context);
                    if (!TextUtils.isEmpty(shareGroupId)) {
                        MixpanelEvent.locShareJoin(LOC_SHARE_JOIN_FROM_COPY, LOC_SHARE_JOIN_TYPE_LOC_SHARE);
                    } else if (!TextUtils.isEmpty(shareName) && context.getString(R.string.ipsmap_dialog_content1).equals(shareName)) {
                        MixpanelEvent.locShareJoin(LOC_SHARE_JOIN_FROM_COPY, LOC_SHARE_JOIN_TYPE_FRIEND_LOCATION);
                    } else {
                        MixpanelEvent.locShareJoin(LOC_SHARE_JOIN_FROM_COPY, LOC_SHARE_JOIN_TYPE_LOCATION_REGION);
                    }
                }, (dialog, which) -> IpsUtils.clearClipboard(context));
            }
        }
    }

    public static class Configuration {
        private Context context;
        private String appKey;
        private boolean debug;
        private ShareToWechatListener shareToWechatListener;

        public static final class Builder {
            private Context context;
            private String appKey;
            private boolean debug;
            private ShareToWechatListener shareToWechatListener;

            public Builder appKey(String appKey) {
                this.appKey = appKey;
                return this;
            }

            public Builder shareToWechatListener(ShareToWechatListener shareToWechatListener) {
                this.shareToWechatListener = shareToWechatListener;
                return this;
            }

            public Builder debug(boolean debug) {
                this.debug = debug;
                return this;
            }

            public Builder(Context context) {
                this.context = context;
            }

            public Configuration build() {
                return new Configuration(this);
            }
        }

        public Configuration(Builder builder) {
            this.context = builder.context;
            this.appKey = builder.appKey;
            this.debug = builder.debug;
            this.shareToWechatListener = builder.shareToWechatListener;
            XJLocationSDK.init(new XJLocationSDK.Configuration.Builder(context)
                    .appKey(appKey)
                    .debug(debug)
                    .hasShareListener(shareToWechatListener != null)
                    .build());
        }
    }

    public static void openXJMapActivity(Context context, String mapId) {
        Intent intent = new Intent(context, XJMapActivity.class);
        intent.putExtra(XJMapActivity.REQUEST_ID, mapId);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void openXJMapActivity(Context context, String mapId, String targetId) {
        Intent intent = new Intent(context, XJMapActivity.class);
        intent.putExtra(XJMapActivity.REQUEST_ID, mapId);
        intent.putExtra(XJMapActivity.REQUEST_TARGET_ID, targetId);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static ShareToWechatListener getShareToWechatListener() {
        return configuration.shareToWechatListener;
    }

    public static void shareLinkToMapView(Intent i) {
        if (Intent.ACTION_VIEW.equals(i.getAction())) {
            Uri uri = i.getData();
            if (uri != null) {
                String mapId = uri.getQueryParameter(XJMapActivity.REQUEST_ID);
                String groupId = uri.getQueryParameter(XJMapActivity.REQUEST_SHARE_LOC_GROUP_ID);
                String name = uri.getQueryParameter(XJMapActivity.REQUEST_SHARE_LOC_NAME);
                String floor = uri.getQueryParameter(XJMapActivity.REQUEST_SHARE_LOC_FLOOR);
                String lat = uri.getQueryParameter(XJMapActivity.REQUEST_SHARE_LOC_LAT);
                String lng = uri.getQueryParameter(XJMapActivity.REQUEST_SHARE_LOC_LNG);
                Intent intent = new Intent(context, XJMapActivity.class);
                intent.putExtra(XJMapActivity.REQUEST_ID, mapId);
                intent.putExtra(XJMapActivity.REQUEST_SHARE_LOC_GROUP_ID, groupId);
                intent.putExtra(XJMapActivity.REQUEST_SHARE_LOC_NAME, name);
                intent.putExtra(XJMapActivity.REQUEST_SHARE_LOC_FLOOR, floor);
                intent.putExtra(XJMapActivity.REQUEST_SHARE_LOC_LAT, lat);
                intent.putExtra(XJMapActivity.REQUEST_SHARE_LOC_LNG, lng);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                if (!TextUtils.isEmpty(groupId)) {
                    MixpanelEvent.locShareJoin(LOC_SHARE_JOIN_FROM_URL, LOC_SHARE_JOIN_TYPE_LOC_SHARE);
                } else if (!TextUtils.isEmpty(name) && context.getString(R.string.ipsmap_dialog_content1).equals(name)) {
                    MixpanelEvent.locShareJoin(LOC_SHARE_JOIN_FROM_URL, LOC_SHARE_JOIN_TYPE_FRIEND_LOCATION);
                } else {
                    MixpanelEvent.locShareJoin(LOC_SHARE_JOIN_FROM_URL, LOC_SHARE_JOIN_TYPE_LOCATION_REGION);
                }
            }
        }
    }


}