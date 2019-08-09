package com.locnavi.watch.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.locnavi.location.xunjimap.utils.IpsUtils;
import com.locnavi.location.xunjimap.utils.T;
import com.locnavi.watch.R;
//import com.locnavi.watch.R;



/**
 * author:chen
 * time:2017/6/22
 * desc:
 */
public class ShareHandler {

    public static void shareToWechat(Context context, String title, String content) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage("com.tencent.mm");

        if (IpsUtils.isWeixinAvilible(context)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, title);
            intent.putExtra(Intent.EXTRA_TEXT, content);
            IpsUtils.copyToClipboard(context, content);
            new Handler().postDelayed(() -> {
                T.showShort(R.string.ipsmap_copy_success);
            }, 2000);
            context.startActivity(intent);
        } else {
            T.showShort(context.getString(R.string.ipsmap_no_weichart));
        }
    }

    public static void shareToQQ(Context context, String title, String content) {
        ComponentName comp = new ComponentName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity");
        Intent intent = new Intent();
        intent.setComponent(comp);
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, title);
        intent.putExtra(Intent.EXTRA_TEXT, content);
        if (IpsUtils.isQQClientAvailable(context)) {
            context.startActivity(intent);
        } else {
            T.showShort(context.getString(R.string.ipsmap_no_qq));
        }
    }

    public static void shareToMsg(Context context, String content) {
        Intent smsIntent = new Intent();
        smsIntent.setAction(Intent.ACTION_VIEW);
        smsIntent.putExtra("sms_body", content);
        smsIntent.setType("vnd.android-dir/mms-sms");
        context.startActivity(smsIntent);
    }
}
