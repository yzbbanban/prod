package com.pl.prod.presenter;


import com.pl.prod.contract.IRegisterContract;
import com.pl.prod.entity.UserRegister;
import com.pl.prod.model.RegisterModelImpl;
import com.pl.prod.model.callback.ICallback;

/**
 * Created by brander on 2017/11/15.
 */

public class RegisterPresenterImpl implements IRegisterContract.Presenter {
    private IRegisterContract.View view;
    private IRegisterContract.Model model;

    public RegisterPresenterImpl(IRegisterContract.View view) {
        this.view = view;
        model = new RegisterModelImpl();
    }

    @Override
    public void registerTask(UserRegister userRegister) throws Exception {
        model.register(userRegister, new ICallback() {
            @Override
            public void setSuccess(Object message) {
                view.showDescription((String) message);
            }

            @Override
            public void setFailure(Object message) {
                view.showDescription((String) message);
            }
        });
    }
}
