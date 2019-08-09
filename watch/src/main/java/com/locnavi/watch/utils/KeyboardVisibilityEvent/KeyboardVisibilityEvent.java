package com.locnavi.watch.utils.KeyboardVisibilityEvent;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

import com.locnavi.location.xunjimap.utils.DensityUtils;

/**
 * author:chen
 * time:2017/10/30
 * desc:
 */
public class KeyboardVisibilityEvent {
    private final static int KEYBOARD_VISIBLE_THRESHOLD_DP = 100;


    /**
     * Set keyboard visibility change event listener.
     *
     * @param activity Activity
     * @param listener KeyboardVisibilityEventListener
     * @return Unregistrar
     */
    public static Unregister registerEventListener(final Activity activity,
                                                   final KeyboardVisibilityEventListener listener) {

        if (activity == null) {
            throw new NullPointerException("Parameter:activity must not be null");
        }

        int softInputMethod = activity.getWindow().getAttributes().softInputMode;
        if (WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE != softInputMethod &&
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED != softInputMethod) {
            throw new IllegalArgumentException("Parameter:activity window SoftInputMethod is not ADJUST_RESIZE");
        }

        if (listener == null) {
            throw new NullPointerException("Parameter:listener must not be null");
        }

        final View activityRoot = getActivityRoot(activity);

        final ViewTreeObserver.OnGlobalLayoutListener layoutListener =
                new ViewTreeObserver.OnGlobalLayoutListener() {

                    private final Rect r = new Rect();

                    private final int visibleThreshold = Math.round(
                            DensityUtils.dp2px(activity, KEYBOARD_VISIBLE_THRESHOLD_DP));

                    private boolean wasOpened = false;

                    @Override
                    public void onGlobalLayout() {
                        activityRoot.getWindowVisibleDisplayFrame(r);

                        int heightDiff = activityRoot.getRootView().getHeight() - r.height();

                        boolean isOpen = heightDiff > visibleThreshold;

                        if (isOpen == wasOpened) {
                            // keyboard state has not changed
                            return;
                        }

                        wasOpened = isOpen;

                        listener.onVisibilityChanged(isOpen);
                    }
                };
        activityRoot.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);

        return new SimpleUnregister(activity, layoutListener);
    }

    /**
     * Determine if keyboard is visible
     *
     * @param activity Activity
     * @return Whether keyboard is visible or not
     */
    public static boolean isKeyboardVisible(Activity activity) {
        Rect r = new Rect();

        View activityRoot = getActivityRoot(activity);
        int visibleThreshold =
                Math.round(DensityUtils.dp2px(activity, KEYBOARD_VISIBLE_THRESHOLD_DP));

        activityRoot.getWindowVisibleDisplayFrame(r);

        int heightDiff = activityRoot.getRootView().getHeight() - r.height();

        return heightDiff > visibleThreshold;
    }

    static View getActivityRoot(Activity activity) {
        return ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
    }
}
