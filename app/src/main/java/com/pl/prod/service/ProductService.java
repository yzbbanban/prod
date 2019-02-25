package com.pl.prod.service;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;
import rx.Observable;

/**
 * 产品
 */
public interface ProductService {
    @GET("list")
    Observable<ResponseBody> productList(
            @Query("pageNo") Integer pageNo,
            @Query("pageSize") Integer pageSize,
            @Header("Authorization") String token
    );

    @GET("update")
    Observable<ResponseBody> productUpdate(
            @Query("id") Integer id,
            @Query("ssid") String ssid,
            @Query("detail") String detail,
            @Query("remark") String remark,
            @Query("typeId") Integer typeId,
            @Header("Authorization") String token
    );
}
