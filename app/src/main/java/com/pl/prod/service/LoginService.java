package com.pl.prod.service;


import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by brander on 2017/8/3.
 * 登录
 */
public interface LoginService {
    @POST("login")
    @FormUrlEncoded
    Observable<ResponseBody> login(
            @Field("mobile") String mobile,
            @Field("countryCode") String countryCode,
            @Field("password") String password
    );
}