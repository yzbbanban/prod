package com.pl.prod.service;


import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by brander on 2017/8/3.
 * 获取 ip token
 */
public interface IpTokenService {
    @GET("info")
    Observable<ResponseBody> getToken();
}