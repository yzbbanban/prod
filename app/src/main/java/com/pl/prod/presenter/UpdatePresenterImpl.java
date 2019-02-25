package com.pl.prod.presenter;


import com.pl.prod.contract.IUpdateContract;
import com.pl.prod.entity.VersionRecord;
import com.pl.prod.model.UpdateModelImpl;
import com.pl.prod.model.callback.ICallback;

/**
 * Created by brander on 2017/11/26.
 */

public class UpdatePresenterImpl implements IUpdateContract.Presenter {
    private IUpdateContract.View view;
    private IUpdateContract.Model model;

    public UpdatePresenterImpl(IUpdateContract.View view) {
        this.view = view;
        this.model = new UpdateModelImpl();
    }

    @Override
    public void updateTask(Integer type) {
        model.update(type, new ICallback() {
            @Override
            public void setSuccess(Object message) {
                view.showUpdateMsg((VersionRecord) message);
            }

            @Override
            public void setFailure(Object message) {
                view.showError((String) message);
            }
        });
    }
}
