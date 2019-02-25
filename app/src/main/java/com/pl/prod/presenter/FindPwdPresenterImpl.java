package com.pl.prod.presenter;


import com.pl.prod.contract.IFindPwdContract;

import com.pl.prod.entity.UserModify;
import com.pl.prod.model.FindPwdModelImpl;
import com.pl.prod.model.callback.ICallback;

/**
 * Created by brander on 2017/11/15.
 */

public class FindPwdPresenterImpl implements IFindPwdContract.Presenter {
    private IFindPwdContract.View view;
    private IFindPwdContract.Model model;

    public FindPwdPresenterImpl(IFindPwdContract.View view) {
        this.view = view;
        model = new FindPwdModelImpl();
    }

    @Override
    public void findPwd(UserModify userModify) throws Exception {
        model.findPwd(userModify, new ICallback() {
            @Override
            public void setSuccess(Object message) {
                view.showFindPwd((String) message);
            }

            @Override
            public void setFailure(Object message) {
                view.showError((String) message);
            }
        });
    }
}
