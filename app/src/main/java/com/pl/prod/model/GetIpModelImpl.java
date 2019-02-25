package com.pl.prod.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pl.prod.app.PlApplication;
import com.pl.prod.consts.IpServerUrls;
import com.pl.prod.contract.IGetIpContract;
import com.pl.prod.entity.ResultJson;
import com.pl.prod.model.callback.ICallback;
import com.pl.prod.service.GetIpService;
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
 * Created by brander on 2017/11/15.
 * 获取 ip
 */
public class GetIpModelImpl implements IGetIpContract.Model {
    private static final String TAG = "ILoginModel";
    private int count = 0;

    @Override
    public void getIp(String deviceId, final ICallback callback) throws Exception {
        if (NetWorkUtils.isConnected(PlApplication.app)) {
            getIpMsg(deviceId, callback, IpServerUrls.GET_IP);
        } else {
            callback.setFailure("0");
        }
    }

    private void getIpMsg(final String deviceId, final ICallback callback, String url) {
        LogUtil.info(TAG, "getIpMsg: " + deviceId + ":" + url);
        Retrofit retrofit = RetrofitUtils.getRetrofit(url);
        final GetIpService request = retrofit.create(GetIpService.class);
        request.getIp(deviceId, PlApplication.ipToken)//获取Observable对象
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
                        callback.setFailure("ip 获取失败");
                    }

                    @Override
                    public void onNext(ResponseBody resultString) {
                        //请求成功
                        Gson gson = new Gson();
                        ResultJson<String> resultJson = null;
                        try {
                            resultJson = gson.fromJson(resultString.string(), new TypeToken<ResultJson<String>>() {
                            }.getType());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        LogUtil.info(TAG, "onNext----》: " + resultJson);
                        callback.setSuccess(resultJson.getData());
                    }
                });
    }


}
