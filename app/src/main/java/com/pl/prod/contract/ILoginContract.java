package com.pl.prod.contract;

import com.pl.prod.entity.User;
import com.pl.prod.model.callback.ICallback;
import com.pl.prod.presenter.BasePresenter;
import com.pl.prod.view.BaseView;

/**
 * Created by brander on 2017/11/15.
 * 登录
 */
public interface ILoginContract {
    interface Model {
        void login(User user, ICallback callback) throws Exception;
    }

    interface View extends BaseView {
        void showLogin(String msg);
    }

    interface Presenter extends BasePresenter {
        void loginTask(User user) throws Exception;
    }
}
