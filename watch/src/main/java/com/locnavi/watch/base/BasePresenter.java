package com.locnavi.watch.base;


import com.parse.ParseException;
import com.locnavi.location.xunjimap.utils.IpsException;

/**
 * author:chen
 * time:2017/6/29
 * desc:
 */
public class BasePresenter<T extends IBaseView> implements IPresenter<T> {

    private T mView;

    @Override
    public void attachView(T mvpView) {
        this.mView = mvpView;
    }

    @Override
    public void detachView() {
        this.mView = null;
    }

    public boolean isViewAttached() {
        return mView != null;
    }

    public T getView() {
        return mView;
    }

    public void checkViewAttached() {
        if (!isViewAttached()) throw new MvpViewNotAttachedException();
    }


    public static class MvpViewNotAttachedException extends RuntimeException {
        public MvpViewNotAttachedException() {
            super("Please call Presenter.attachView(MvpView) before" +
                    " requesting data to the Presenter");
        }
    }

    public void handleError(ParseException e) {
        e.printStackTrace();
        IpsException ipsException = new IpsException(e, e.getCode());
        if (isViewAttached()) {
            getView().handleThrowable(ipsException);
        }
    }


}