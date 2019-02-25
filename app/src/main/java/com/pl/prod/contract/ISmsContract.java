package com.pl.prod.contract;

import com.pl.prod.entity.SmsMessage;
import com.pl.prod.model.callback.ICallback;
import com.pl.prod.presenter.BasePresenter;
import com.pl.prod.view.BaseView;

/**
 * Created by brander on 2017/11/15.
 * 注册
 */
public interface ISmsContract {
    interface Model {
        /**
         * 发送短信
         *
         * @param smsMessage 短信
         * @param type       短信类型
         * @param callback   回调
         * @throws Exception 异常
         */
        void sendSms(SmsMessage smsMessage, int type, ICallback callback) throws Exception;
    }

    interface View extends BaseView {

    }

    interface Presenter extends BasePresenter {
        void smsTask(SmsMessage smsMessage, int type) throws Exception;
    }
}
