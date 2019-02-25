package com.pl.prod.contract;

import com.pl.prod.entity.UserRegister;
import com.pl.prod.model.callback.ICallback;
import com.pl.prod.presenter.BasePresenter;
import com.pl.prod.view.BaseView;

/**
 * Created by brander on 2017/11/15.
 * 注册
 */
public interface IRegisterContract {
    interface Model {
        void register(UserRegister userRegister, ICallback callback) throws Exception;
    }

    interface View extends BaseView {
    }

    interface Presenter extends BasePresenter {
        void registerTask(UserRegister userRegister) throws Exception;
    }
}
