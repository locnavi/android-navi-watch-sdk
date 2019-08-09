package com.locnavi.watch.ui.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

/**
 *
 */
public class ImageLoader {

    public static void load(Context context, String url, ImageView iv) {    //使用Glide加载圆形ImageView(如头像)时，不要使用占位图
        Glide.with(context).load(url).crossFade().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(iv);
    }

    public static void load(Context context, String url, ImageView iv, int placeholder) {
        Glide.with(context)
                .load(url)
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(placeholder)
                .crossFade(500)
                .into(new GlideDrawableImageViewTarget(iv) {
                    @Override
                    public void onResourceReady(GlideDrawable resource,
                                                GlideAnimation<? super GlideDrawable> animation) {
                        super.onResourceReady(resource, new PaddingAnimation<>(animation));
                    }
                });
    }

    public static void load(Context context, int drwable, ImageView iv) {
        //使用Glide加载本地图片
        Glide.with(context).load(drwable).crossFade().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(iv);
    }


//    public static void loadSmall(Context context, String url, ImageView iv) {    //使用Glide加载圆形ImageView(如头像)时，不要使用占位图
//        load(context,url,iv, R.drawable.ic_logo_small);
//    }
//
//    public static void loadBig(Context context, String url, ImageView iv) {    //使用Glide加载圆形ImageView(如头像)时，不要使用占位图
//        load(context,url,iv,R.drawable.ic_logo_big);
//    }

    public static void load(Activity activity, String url, ImageView iv) {    //使用Glide加载圆形ImageView(如头像)时，不要使用占位图
        Glide.with(activity).load(url).crossFade().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(iv);
    }

    public static void loadCircle(Context context, String url, ImageView iv) {    //使用Glide加载圆形ImageView(如头像)时，不要使用占位图
        Glide.with(context).load(url).crossFade().diskCacheStrategy(DiskCacheStrategy.SOURCE).transform(new GlideCircleTransform(context)).into(iv);
    }

    public static void loadAll(Context context, String url, ImageView iv) {    //不缓存，全部从网络加载
        Glide.with(context).load(url).crossFade().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(iv);
    }

    public static void loadAll(Activity activity, String url, ImageView iv) {    //不缓存，全部从网络加载
        Glide.with(activity).load(url).crossFade().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(iv);
    }
}
