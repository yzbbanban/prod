package com.pl.prod.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pl.prod.app.PlApplication;
import com.pl.prod.consts.IpServerUrls;
import com.pl.prod.consts.TcpServerUrls;
import com.pl.prod.contract.IFindPwdContract;
import com.pl.prod.contract.IGetIpContract;
import com.pl.prod.entity.ResultJson;
import com.pl.prod.entity.User;
import com.pl.prod.entity.UserModify;
import com.pl.prod.model.callback.ICallback;
import com.pl.prod.service.FindPwdService;
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
 * 登陆
 */

public class FindPwdModelImpl implements IFindPwdContract.Model {
    private static final String TAG = "ILoginModel";
    private int count = 0;

    @Override
    public void findPwd(UserModify userModify, ICallback callback) throws Exception {
        if (NetWorkUtils.isConnected(PlApplication.app)) {
            findPwd(userModify, callback, TcpServerUrls.getAppFtpFindPwd());
        } else {
            callback.setFailure("0");
        }
    }

    private void findPwd(final UserModify userModify, final ICallback callback, String url) {
        LogUtil.info(TAG, "getIpMsg: " + userModify + ":" + url);
        Retrofit retrofit = RetrofitUtils.getRetrofit(url);
        final FindPwdService request = retrofit.create(FindPwdService.class);
        request.findPwd(userModify.getPassword(), userModify.getMobile(), userModify.getCountryCode(), userModify.getCode())//获取Observable对象
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
                        callback.setFailure("2");
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
                        if (resultJson.getCode().intValue() == 200) {
                            callback.setSuccess(resultJson.getMessage());
                        } else {
                            callback.setFailure(resultJson.getMessage());
                        }
                    }
                });
    }


}
