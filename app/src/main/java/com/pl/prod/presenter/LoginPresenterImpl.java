package com.pl.prod.presenter;


import com.pl.prod.contract.ILoginContract;
import com.pl.prod.entity.User;
import com.pl.prod.model.LoginModelImpl;
import com.pl.prod.model.callback.ICallback;

/**
 * Created by brander on 2017/11/15.
 */

public class LoginPresenterImpl implements ILoginContract.Presenter {
    private ILoginContract.View view;
    private ILoginContract.Model model;

    public LoginPresenterImpl(ILoginContract.View view) {
        this.view = view;
        model = new LoginModelImpl();
    }

    @Override
    public void loginTask(User user) throws Exception {
        model.login(user, new ICallback() {
            @Override
            public void setSuccess(Object message) {
                view.showLogin((String) message);
            }

            @Override
            public void setFailure(Object message) {
                view.showError((String) message);
            }
        });
    }
}
