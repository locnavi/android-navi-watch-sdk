package com.locnavi.watch.base;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.parse.ParseException;
import com.locnavi.location.xunjimap.utils.IpsException;
import com.locnavi.location.xunjimap.utils.T;
import com.locnavi.location.xunjimap.utils.TUtil;

/**
 * author:chen
 * time:2017/6/29
 * desc:
 */
public abstract class BasePresenterFragment<P extends BasePresenter> extends BaseFragment implements IBaseView {
    public P presenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        presenter = TUtil.getT(this, 0);
        presenter.attachView(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        presenter.detachView();
        super.onDestroy();
    }

    @Override
    public void handleThrowable(IpsException ex) {
        String message = ex.getMsg();
        if (ex.getCode() != ParseException.CACHE_MISS) {
            T.showShort(message);
        }
    }
}
