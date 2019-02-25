package com.pl.prod.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pl.prod.app.PlApplication;
import com.pl.prod.consts.TcpServerUrls;
import com.pl.prod.contract.IFindPwdContract;
import com.pl.prod.contract.ISmsContract;
import com.pl.prod.entity.ResultJson;
import com.pl.prod.entity.SmsMessage;
import com.pl.prod.entity.User;
import com.pl.prod.entity.UserModify;
import com.pl.prod.model.callback.ICallback;
import com.pl.prod.service.FindPwdService;
import com.pl.prod.service.FindPwdSmsService;
import com.pl.prod.service.RegisterSmsService;
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
 * 短信发送
 */

public class SmsModelImpl implements ISmsContract.Model {
    private static final String TAG = "ILoginModel";
    private int count = 0;


    @Override
    public void sendSms(SmsMessage smsMessage, int type, ICallback callback) throws Exception {

        if (NetWorkUtils.isConnected(PlApplication.app)) {
            send(smsMessage, callback, type);
        } else {
            callback.setFailure("0");
        }
    }

    private void send(final SmsMessage smsMessage, final ICallback callback, int type) {
        LogUtil.info(TAG, "send: " + smsMessage);
        Retrofit retrofit = null;
        switch (type) {
            case 1:
                retrofit = RetrofitUtils.getRetrofit(TcpServerUrls.getAppFtpSmsRegister());
                final RegisterSmsService reRequest = retrofit.create(RegisterSmsService.class);
                reRequest.registerSms(smsMessage.getPhoneNumber(), smsMessage.getCountryCode())//获取Observable对象
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
                                callback.setFailure("请重试");
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

                break;
            case 2:
                retrofit = RetrofitUtils.getRetrofit(TcpServerUrls.getAppFtpSmsFindPwd());
                final FindPwdSmsService request = retrofit.create(FindPwdSmsService.class);
                request.findPwdSms(smsMessage.getPhoneNumber(), smsMessage.getCountryCode())//获取Observable对象
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
                                callback.setFailure("请重试");
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

                break;
            default:
                break;
        }


    }


}
