package com.pl.prod.model;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pl.prod.app.PlApplication;
import com.pl.prod.consts.IpServerUrls;
import com.pl.prod.contract.IUpdateContract;
import com.pl.prod.entity.ResultJson;
import com.pl.prod.entity.VersionRecord;
import com.pl.prod.model.callback.ICallback;
import com.pl.prod.service.PlUpdateService;
import com.pl.prod.utils.LogUtil;
import com.pl.prod.utils.NetWorkUtils;
import com.pl.prod.utils.retrofit.RetrofitUtils;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by brander on 2017/11/26.
 * 版本更新接口
 */

public class UpdateModelImpl implements IUpdateContract.Model {
    private static final String TAG = "IUpdateModel";


    @Override
    public void update(Integer type, final ICallback callback) {
        if (NetWorkUtils.isConnected(PlApplication.app)) {
            getUpdateMsg(type, callback);
        } else {
            callback.setFailure("0");
        }
    }

    private void getUpdateMsg(Integer type, final ICallback callback) {
        Retrofit retrofit = RetrofitUtils.getRetrofit(IpServerUrls.GET_VERSION);
        final PlUpdateService request = retrofit.create(PlUpdateService.class);
        request.call(PlApplication.ipToken, type)//获取Observable对象
                .subscribeOn(Schedulers.newThread())//请求在新的线程中执行
                .observeOn(Schedulers.io())         //请求完成后在io线程中执行
                .doOnNext(new Action1<ResponseBody>() {
                    @Override
                    public void call(ResponseBody resultString) {

                    }
                })
                .observeOn(AndroidSchedulers.mainThread())//最后在主线程中执行
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {
                        LogUtil.info(TAG, "onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        //请求失败
                        LogUtil.info(TAG, " loading onError: " + e.getMessage());
//                        callback.setFailure("4");
                    }

                    @Override
                    public void onNext(ResponseBody resultString) {
                        try {
                            //请求成功
                            String data = resultString.string();
                            Gson gson = new Gson();
                            ResultJson<VersionRecord> resultJson = null;
                            try {
                                resultJson = gson.fromJson(data, new TypeToken<ResultJson<VersionRecord>>() {
                                }.getType());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            LogUtil.info(TAG, "loading onNext: " + resultJson);
                            if (resultJson.getCode().compareTo(200) == 0) {
                                callback.setSuccess(resultJson.getData());
                            } else {
                                callback.setFailure(resultJson.getMessage());
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });
    }
}
