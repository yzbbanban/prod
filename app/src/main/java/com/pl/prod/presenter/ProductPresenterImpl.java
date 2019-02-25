package com.pl.prod.presenter;


import com.pl.prod.contract.IProductContract;
import com.pl.prod.entity.PageParam;
import com.pl.prod.model.ProductModelImpl;
import com.pl.prod.model.callback.ICallback;

/**
 * Created by brander on 2017/11/15.
 */

public class ProductPresenterImpl implements IProductContract.Presenter {
    private IProductContract.View view;
    private IProductContract.Model model;

    public ProductPresenterImpl(IProductContract.View view) {
        this.view = view;
        model = new ProductModelImpl();
    }


    @Override
    public void productTask(PageParam pageParam) throws Exception {
        model.product(pageParam, new ICallback() {
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
