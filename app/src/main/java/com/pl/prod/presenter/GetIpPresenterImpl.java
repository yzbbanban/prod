package com.pl.prod.presenter;


import com.pl.prod.contract.IGetIpContract;
import com.pl.prod.contract.ILoginContract;
import com.pl.prod.entity.User;
import com.pl.prod.model.GetIpModelImpl;
import com.pl.prod.model.LoginModelImpl;
import com.pl.prod.model.callback.ICallback;

/**
 * Created by brander on 2017/11/15.
 */

public class GetIpPresenterImpl implements IGetIpContract.Presenter {
    private IGetIpContract.View view;
    private IGetIpContract.Model model;

    public GetIpPresenterImpl(IGetIpContract.View view) {
        this.view = view;
        model = new GetIpModelImpl();
    }


    @Override
    public void getIpTask(String deviceId) throws Exception {
        model.getIp(deviceId, new ICallback() {
            @Override
            public void setSuccess(Object message) {
                view.showIp((String) message);
            }

            @Override
            public void setFailure(Object message) {
                view.showError((String) message);
            }
        });
    }
}
