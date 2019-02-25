package com.pl.prod.presenter;


import com.pl.prod.contract.ISmsContract;
import com.pl.prod.entity.SmsMessage;
import com.pl.prod.model.SmsModelImpl;
import com.pl.prod.model.callback.ICallback;

/**
 * Created by brander on 2017/11/15.
 */

public class SmsPresenterImpl implements ISmsContract.Presenter {
    private ISmsContract.View view;
    private ISmsContract.Model model;

    public SmsPresenterImpl(ISmsContract.View view) {
        this.view = view;
        model = new SmsModelImpl();
    }


    @Override
    public void smsTask(SmsMessage smsMessage, int type) throws Exception {
        model.sendSms(smsMessage, type, new ICallback() {
            @Override
            public void setSuccess(Object message) {
                view.showDescription((String) message);
            }

            @Override
            public void setFailure(Object message) {
                view.showError((String) message);
            }
        });
    }

}
