package com.locnavi.watch.ui.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;


import com.locnavi.location.xunjimap.utils.DensityUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class BitmapUtils {

    private static Drawable drawable;

    /**
     * @param options   参数
     * @param reqWidth  目标的宽度
     * @param reqHeight 目标的高度
     * @return
     * @description 计算图片的压缩比率
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    /**
     * @param src
     * @param dstWidth
     * @param dstHeight
     * @return
     * @description 通过传入的bitmap，进行压缩，得到符合标准的bitmap
     */
    private static Bitmap createScaleBitmap(Bitmap src, int dstWidth, int dstHeight, int inSampleSize) {
        //如果inSampleSize是2的倍数，也就说这个src已经是我们想要的缩略图了，直接返回即可。
        if (inSampleSize % 2 == 0) {
            return src;
        }
        // 如果是放大图片，filter决定是否平滑，如果是缩小图片，filter无影响，我们这里是缩小图片，所以直接设置为false
        Bitmap dst = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, false);
        if (src != dst) { // 如果没有缩放，那么不回收
            src.recycle(); // 释放Bitmap的native像素数组
        }
        return dst;
    }

    public static Bitmap getBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 设置成了true,不占用内存，只获取bitmap宽高
        BitmapFactory.decodeResource(res, resId, options); // 读取图片长款
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight); // 调用上面定义的方法计算inSampleSize值
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        Bitmap src = BitmapFactory.decodeResource(res, resId, options); // 载入一个稍大的缩略图
        return createScaleBitmap(src, reqWidth, reqHeight, options.inSampleSize); // 进一步得到目标大小的缩略图
    }

    public static Bitmap getBitmapFromResource(Resources res, int resId) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static Bitmap setScale(Bitmap bitmap, float scale) {
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale); //长和宽放大缩小的比例
        int width = bitmap.getWidth();
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizeBmp;
    }

    public static Bitmap drawableToBitmap(Drawable drawable ,Context context) {
        BitmapUtils.drawable = drawable;
//        if (drawable instanceof BitmapDrawable) {
//            return ((BitmapDrawable) drawable).getBitmap();
//        }

        int intrinsicWidth = drawable.getIntrinsicWidth();
        float insWidthDp = DensityUtils.px2dp(context, intrinsicWidth);
        int insWidthPx = DensityUtils.dp2px(context, intrinsicWidth);

        int intrinsicHeight = drawable.getIntrinsicHeight();
        float inHeightDp = DensityUtils.px2dp(context, intrinsicHeight);
        int inHeightPx = DensityUtils.dp2px(context, intrinsicHeight);
//        Bitmap bitmap = Bitmap.createBitmap((int) insWidthDp, (int)inHeightDp, Bitmap.Config.ARGB_8888);
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);


        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static Bitmap getNewBitMap(String text) {
        Bitmap newBitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawBitmap(newBitmap, 0, 0, null);
        TextPaint textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(80F);
        StaticLayout sl = new StaticLayout(text, textPaint, newBitmap.getWidth() - 8, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
        canvas.translate(6, 40);
        sl.draw(canvas);
        return newBitmap;
    }


    public static Bitmap getBitmapFromFile(String pathName, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        Bitmap src = BitmapFactory.decodeFile(pathName, options);
        return createScaleBitmap(src, reqWidth, reqHeight, options.inSampleSize);
    }

    public static byte[] getByteFromFile(String pathName) {
        Bitmap bm = getBitmapFromFile(pathName, 500, 400);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }


    public static final String TEMP_IMG_NAME = "temp.png";

    public static void saveBitmap(Context context, Bitmap bm, SaveBitmapListener saveBitmapListener) {
        File file = new File(getCacheImgPath(context));
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 50, fos);
            saveBitmapListener.onSuccess();
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static String getCacheImgPath(Context context) {
        return context.getCacheDir().getAbsolutePath() + "/" + TEMP_IMG_NAME;
    }

    public interface SaveBitmapListener {
        void onSuccess();
    }

    public static class Size {
        public int width;
        public int height;

        public Size(int w, int h) {
            this.width = w;
            this.height = h;
        }
    }

    public static Bitmap getImage(String absPath) {
        Bitmap bitmap = BitmapFactory.decodeFile(absPath);
        return bitmap;
    }

    public static Size getImageSize(String absPath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ALPHA_8;
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(absPath, options);
        Size size = new Size(options.outWidth, options.outHeight);
        return size;
    }

    public static Bitmap blur(Bitmap bitmap) {
        int iterations = 1;
        int radius = 8;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] inPixels = new int[width * height];
        int[] outPixels = new int[width * height];
        Bitmap blured = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.getPixels(inPixels, 0, width, 0, 0, width, height);
        for (int i = 0; i < iterations; i++) {
            blur(inPixels, outPixels, width, height, radius);
            blur(outPixels, inPixels, height, width, radius);
        }
        blured.setPixels(inPixels, 0, width, 0, 0, width, height);
        return blured;
    }

    private static void blur(int[] in, int[] out, int width, int height,
                             int radius) {
        int widthMinus1 = width - 1;
        int tableSize = 2 * radius + 1;
        int divide[] = new int[256 * tableSize];

        for (int index = 0; index < 256 * tableSize; index++) {
            divide[index] = index / tableSize;
        }

        int inIndex = 0;

        for (int y = 0; y < height; y++) {
            int outIndex = y;
            int ta = 0, tr = 0, tg = 0, tb = 0;

            for (int i = -radius; i <= radius; i++) {
                int rgb = in[inIndex + clamp(i, 0, width - 1)];
                ta += (rgb >> 24) & 0xff;
                tr += (rgb >> 16) & 0xff;
                tg += (rgb >> 8) & 0xff;
                tb += rgb & 0xff;
            }

            for (int x = 0; x < width; x++) {
                out[outIndex] = (divide[ta] << 24) | (divide[tr] << 16)
                        | (divide[tg] << 8) | divide[tb];

                int i1 = x + radius + 1;
                if (i1 > widthMinus1)
                    i1 = widthMinus1;
                int i2 = x - radius;
                if (i2 < 0)
                    i2 = 0;
                int rgb1 = in[inIndex + i1];
                int rgb2 = in[inIndex + i2];

                ta += ((rgb1 >> 24) & 0xff) - ((rgb2 >> 24) & 0xff);
                tr += ((rgb1 & 0xff0000) - (rgb2 & 0xff0000)) >> 16;
                tg += ((rgb1 & 0xff00) - (rgb2 & 0xff00)) >> 8;
                tb += (rgb1 & 0xff) - (rgb2 & 0xff);
                outIndex += height;
            }
            inIndex += width;
        }
    }

    private static int clamp(int x, int a, int b) {
        return (x < a) ? a : (x > b) ? b : x;
    }
}
