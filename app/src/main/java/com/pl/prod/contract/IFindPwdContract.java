package com.pl.prod.contract;

import com.pl.prod.entity.UserModify;
import com.pl.prod.model.callback.ICallback;
import com.pl.prod.presenter.BasePresenter;
import com.pl.prod.view.BaseView;

/**
 * Created by brander on 2017/11/15.
 * 找回密码
 */

public interface IFindPwdContract {
    interface Model {
        void findPwd(UserModify userModify, ICallback callback) throws Exception;
    }

    interface View extends BaseView {
        void showFindPwd(String msg);
    }

    interface Presenter extends BasePresenter {
        void findPwd(UserModify userModify) throws Exception;
    }
}
