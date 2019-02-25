package com.pl.prod.service;


import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by brander on 2017/8/3.
 * 忘记密码
 */
public interface RegisterSmsService {
    @POST("register")
    @FormUrlEncoded
    Observable<ResponseBody> registerSms(
            @Field("phoneNumber") String phoneNumber,
            @Field("countryCode") String countryCode
    );
}