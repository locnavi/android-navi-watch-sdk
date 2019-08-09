package com.locnavi.watch.ui.utils;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * author:chen
 * time:2017/5/10
 * desc:
 */
public class MarginDecoration extends RecyclerView.ItemDecoration {
    private int left = 0, top = 0, right = 0, bottom = 0;

    public MarginDecoration(Context context) {

    }

    public void setItemMargin(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(left, top, right, bottom);
    }
}
