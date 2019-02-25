package com.pl.prod.contract;

import com.pl.prod.entity.User;
import com.pl.prod.model.callback.ICallback;
import com.pl.prod.presenter.BasePresenter;
import com.pl.prod.view.BaseView;

/**
 * Created by brander on 2017/11/15.
 * 获取 ip token
 */
public interface IIpTokenContract {
    interface Model {
        void getIpToken(String deviceId, ICallback callback) throws Exception;
    }

    interface View extends BaseView {
        void showToken(String ipToken);

        void showError(String error);
    }

    interface Presenter extends BasePresenter {
        void getIpTokenTask(String deviceId) throws Exception;
    }
}
