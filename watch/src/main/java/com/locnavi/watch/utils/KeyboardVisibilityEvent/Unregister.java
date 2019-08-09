package com.locnavi.watch.utils.KeyboardVisibilityEvent;

import android.view.ViewTreeObserver;

/**
 * author:chen
 * time:2017/10/30
 * desc:
 */
public interface Unregister {
    /**
     * unregisters the {@link ViewTreeObserver.OnGlobalLayoutListener} and there by does provide any more callback to the {@link KeyboardVisibilityEventListener}
     */
    void unregister();
}
