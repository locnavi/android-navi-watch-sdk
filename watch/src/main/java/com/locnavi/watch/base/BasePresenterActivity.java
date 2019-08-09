package com.locnavi.watch.base;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.locnavi.location.xunjimap.utils.IpsException;
import com.locnavi.location.xunjimap.utils.T;
import com.locnavi.location.xunjimap.utils.TUtil;
import com.locnavi.watch.R;
//import com.xunji.location.ipsmap.utils.IpsException;
//import com.xunji.location.ipsmap.utils.T;
//import com.xunji.location.ipsmap.utils.TUtil;
import com.parse.ParseException;

/**
 * author:chen
 * time:2017/6/29
 * desc:
 */
public abstract class BasePresenterActivity<P extends BasePresenter> extends BaseActivity implements IBaseView {

    public P presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        presenter = TUtil.getT(this, 0);
        presenter.attachView(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        presenter.detachView();
        super.onDestroy();
    }

    @Override
    public void handleThrowable(IpsException ex) {
        String message = ex.getMsg();
        if (ex.getCode() == ParseException.OBJECT_NOT_FOUND) {
            T.showShort(R.string.ipsmap_no_result);
        } else if (ex.getCode() != ParseException.CACHE_MISS) {
            T.showShort(message);
        }
    }

}
