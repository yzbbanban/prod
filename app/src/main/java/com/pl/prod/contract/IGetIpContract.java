package com.pl.prod.contract;

import com.pl.prod.model.callback.ICallback;
import com.pl.prod.presenter.BasePresenter;
import com.pl.prod.view.BaseView;

/**
 * Created by brander on 2017/11/15.
 * 获取 ip
 */
public interface IGetIpContract {
    interface Model {
        void getIp(String deviceId, ICallback callback) throws Exception;
    }

    interface View extends BaseView {
        void showIp(String ip);
    }

    interface Presenter extends BasePresenter {
        void getIpTask(String deviceId) throws Exception;
    }
}
