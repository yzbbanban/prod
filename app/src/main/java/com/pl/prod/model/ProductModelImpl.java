package com.pl.prod.model;

import com.google.gson.Gson;
import com.pl.prod.app.PlApplication;
import com.pl.prod.consts.TcpServerUrls;
import com.pl.prod.contract.IProductContract;
import com.pl.prod.entity.PageParam;
import com.pl.prod.model.callback.ICallback;
import com.pl.prod.service.ProductService;
import com.pl.prod.utils.LogUtil;
import com.pl.prod.utils.NetWorkUtils;
import com.pl.prod.utils.retrofit.RetrofitUtils;

import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by brander on 2017/11/15.
 * 产品
 */
public class ProductModelImpl implements IProductContract.Model {
    private static final String TAG = "ILoginModel";
    private int count = 0;

    @Override
    public void product(PageParam pageParam, ICallback callback) throws Exception {
        if (NetWorkUtils.isConnected(PlApplication.app)) {
            product(pageParam, callback, TcpServerUrls.getAppFtpProductList());
        } else {
            callback.setFailure("0");
        }
    }

    private void product(final PageParam pageParam, final ICallback callback, String url) {
        LogUtil.info(TAG, "pageParam: " + pageParam + ":" + url);
        Retrofit retrofit = RetrofitUtils.getRetrofit(url);
        final ProductService request = retrofit.create(ProductService.class);
        request.productList(pageParam.getPageNo(), pageParam.getPageSize(), PlApplication.userToken)//获取Observable对象
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
                        String data = null;
                        LogUtil.info(TAG, "onNext----》: " + data);
                        callback.setSuccess("1");
                    }
                });
    }


}
