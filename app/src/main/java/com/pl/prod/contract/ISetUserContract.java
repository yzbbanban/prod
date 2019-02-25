package com.pl.prod.contract;

import com.pl.prod.entity.UserModify;
import com.pl.prod.model.callback.ICallback;
import com.pl.prod.presenter.BasePresenter;
import com.pl.prod.view.BaseView;

/**
 * Created by brander on 2017/11/15.
 * 找回密码
 */

public interface ISetUserContract {
    interface Model {
        void setUser(UserModify userModify, ICallback callback) throws Exception;
    }

    interface View extends BaseView {

    }

    interface Presenter extends BasePresenter {
        void setUserPwd(UserModify userModify) throws Exception;
    }
}
