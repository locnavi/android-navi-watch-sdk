package com.locnavi.watch.ui.utils;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;

import com.locnavi.location.xunjimap.utils.DensityUtils;


public class TextViewUtilis {

    public static String setMaxLength(String text, int length) {
        if (text.length() > length) {
            text = text.substring(0, length) + "...";
        }
        return text;
    }

    public static SpannableStringBuilder setPartColor(Context context, String text, int start, int end, int textColor) {
        SpannableStringBuilder sb = new SpannableStringBuilder(text);
        sb.setSpan(new ForegroundColorSpan(context.getResources().getColor(textColor)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }

    public static SpannableStringBuilder setPartColor(Context context, String text, int textColor, int textSize) {
        SpannableStringBuilder sb = new SpannableStringBuilder(text);
        sb.setSpan(new ForegroundColorSpan(context.getResources().getColor(textColor)), 0, text.indexOf("\n"), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        sb.setSpan(new AbsoluteSizeSpan(DensityUtils.sp2px(context, textSize)), 0, text.indexOf("\n"), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }

    public static SpannableStringBuilder setPartSize(Context context, String text, int start, int end, int textSize) {
        SpannableStringBuilder sb = new SpannableStringBuilder(text);
        sb.setSpan(new AbsoluteSizeSpan(DensityUtils.sp2px(context, textSize)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }

    public static SpannableStringBuilder setPartColorSize(Context context, String text, int start, int end, int textColor, int textSize) {
        SpannableStringBuilder sb = new SpannableStringBuilder(text);
        sb.setSpan(new ForegroundColorSpan(context.getResources().getColor(textColor)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        sb.setSpan(new AbsoluteSizeSpan(DensityUtils.sp2px(context, textSize)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }
}
