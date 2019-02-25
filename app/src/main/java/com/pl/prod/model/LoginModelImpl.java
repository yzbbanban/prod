package com.pl.prod.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pl.prod.app.PlApplication;
import com.pl.prod.consts.TcpServerUrls;
import com.pl.prod.contract.ILoginContract;
import com.pl.prod.entity.ResultJson;
import com.pl.prod.entity.User;
import com.pl.prod.model.callback.ICallback;
import com.pl.prod.service.LoginService;
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

public class LoginModelImpl implements ILoginContract.Model {
    private static final String TAG = "ILoginModel";
    private int count = 0;

    @Override
    public void login(User user, final ICallback callback) throws Exception {
        if (NetWorkUtils.isConnected(PlApplication.app)) {
            getLoginMsg(user, callback, TcpServerUrls.getAppFtpLogin());
        } else {
            callback.setFailure("0");
        }
    }

    private void getLoginMsg(final User user, final ICallback callback, String url) {
        LogUtil.info(TAG, "login: " + user.toString() + "\n" + url);
        Retrofit retrofit = RetrofitUtils.getRetrofit(url);
        final LoginService request = retrofit.create(LoginService.class);
        request.login(user.getMobile(), user.getCountryCode(), user.getPassword())//获取Observable对象
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
                        e.printStackTrace();
                        callback.setFailure("登录失败");
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
