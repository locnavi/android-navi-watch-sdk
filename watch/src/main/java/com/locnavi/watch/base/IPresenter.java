package com.locnavi.watch.base;

/**
 * author:chen
 * time:2017/6/29
 * desc:
 */
public interface IPresenter<V extends IBaseView> {

    void attachView(V mvpView);

    void detachView();

}