package com.pl.prod.contract;

import com.pl.prod.entity.PageParam;
import com.pl.prod.entity.Product;
import com.pl.prod.model.callback.ICallback;
import com.pl.prod.presenter.BasePresenter;
import com.pl.prod.view.BaseView;

/**
 * Created by brander on 2017/11/15.
 * 产品
 */
public interface IProductContract {
    interface Model {
        void product(PageParam pageParam, ICallback callback) throws Exception;
    }

    interface View extends BaseView {

    }

    interface Presenter extends BasePresenter {
        void productTask(PageParam pageParam) throws Exception;
    }
}
