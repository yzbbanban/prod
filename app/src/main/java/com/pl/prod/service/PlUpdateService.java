package com.pl.prod.service;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by brander on 2017/8/3.
 * 更新
 */
public interface PlUpdateService {
    @GET("info")
    Observable<ResponseBody> call(
            @Header("Authorization") String authorization,
            @Query("type") Integer type
    );
}