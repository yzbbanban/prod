package com.pl.prod.utils.retrofit;

import com.pl.prod.app.PlApplication;
import com.pl.prod.utils.SystemUtils;
import com.pl.prod.utils.ToastUtil;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;


/**
 * Created by brander on 2017/8/17.
 * 网络请求工具
 */

public class RetrofitUtils {
    public static Retrofit getRetrofit(String url) {

        try {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Interceptor.Chain chain) throws IOException {
                    Request original = chain.request();

                    Request request = original.newBuilder()
                            .header("deviceId", SystemUtils.getUniquePsuedoID())
                            .method(original.method(), original.body())
                            .build();

                    return chain.proceed(request);
                }

            });
            OkHttpClient client = httpClient.build();
            Retrofit retrofit = new Retrofit.Builder()
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())//rxJava
//                .addConverterFactory(GsonConverterFactory.create())
//                .addConverterFactory(SimpleXmlConverterFactory.create())
                    .baseUrl(url)
                    .client(client)
                    .build();
            return retrofit;
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.ToastShow("网络状态错误");
            return null;
        }


    }


}
