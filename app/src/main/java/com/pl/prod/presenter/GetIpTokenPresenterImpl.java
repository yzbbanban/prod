package com.pl.prod.presenter;


import com.pl.prod.contract.IIpTokenContract;
import com.pl.prod.model.GetIpTokenModelImpl;
import com.pl.prod.model.callback.ICallback;

/**
 * Created by brander on 2017/11/15.
 */

public class GetIpTokenPresenterImpl implements IIpTokenContract.Presenter {
    private IIpTokenContract.View view;
    private IIpTokenContract.Model model;

    public GetIpTokenPresenterImpl(IIpTokenContract.View view) {
        this.view = view;
        model = new GetIpTokenModelImpl();
    }


    @Override
    public void getIpTokenTask(String deviceId) throws Exception {
        model.getIpToken(deviceId, new ICallback() {
            @Override
            public void setSuccess(Object message) {
                view.showToken((String) message);
            }

            @Override
            public void setFailure(Object message) {
                view.showError((String) message);
            }
        });
    }
}
