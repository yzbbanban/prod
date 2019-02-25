package com.pl.prod.contract;


import com.pl.prod.entity.VersionRecord;
import com.pl.prod.model.callback.ICallback;
import com.pl.prod.presenter.BasePresenter;
import com.pl.prod.view.BaseView;

/**
 * Created by brander on 2017/9/24.
 * 更新程序
 */
public interface IUpdateContract {
    interface Model {
        void update(Integer type, ICallback callBack);
    }

    interface View extends BaseView {
        void showUpdateMsg(VersionRecord msg);
    }

    interface Presenter extends BasePresenter {
        void updateTask(Integer type);
    }
}
