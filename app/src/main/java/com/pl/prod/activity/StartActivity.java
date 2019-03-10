package com.pl.prod.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pl.prod.R;
import com.pl.prod.app.PlApplication;
import com.pl.prod.contract.IGetIpContract;
import com.pl.prod.contract.IIpTokenContract;
import com.pl.prod.presenter.GetIpPresenterImpl;
import com.pl.prod.presenter.GetIpTokenPresenterImpl;
import com.pl.prod.utils.SharedPreUtil;
import com.pl.prod.utils.SystemUtils;
import com.pl.prod.utils.ToastUtil;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;

import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;

public class StartActivity extends AppCompatActivity implements IGetIpContract.View, IIpTokenContract.View {
    private static final String TAG = "StartActivity";

    @BindView(R.id.btn_jump)
    Button btnJump;
    @BindView(R.id.tv_version)
    TextView tvVersion;

    private MyThread myThread;
    private Thread thread;
    //权限
    private final int SDK_PERMISSION_REQUEST = 127;
    private String permissionInfo;

    //获取 ip
    private IGetIpContract.Presenter getIpPresenter;
    private IIpTokenContract.Presenter ipTokenPresenter;
    private int i = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);
        getPersimmions();
        String version = SystemUtils.getLocalVersionName(this);
        tvVersion.setText(version);
        initData();

    }

    /**
     * 初始化数据，默认3秒开启 app
     */
    private void initData() {

        //加载 ip 获取 ip token
        String ip = SharedPreUtil.getAPiSp(this, PlApplication.user.getMobile());
        if (ip == null || "".equals(ip) || ip.length() > 0) {
            try {
                ipTokenPresenter = new GetIpTokenPresenterImpl(this);
                ipTokenPresenter.getIpTokenTask(SystemUtils.getUniquePsuedoID());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            PlApplication.url = "http://" + ip + ":9095";
        }

    }

//
//    /**
//     * 跳转 login
//     */
//    @OnClick(R.id.btn_jump)
//    public void jump() {
//        Intent intent = new Intent(this, LoginActivity.class);
//        startActivity(intent);
//        finish();
//    }


    /**
     * 权限检查
     */
    private void getPersimmions() {


        //这个请求事件我写在点击事件里面，
        //点击button之后RxPermissions会为我们申请运行时权限
        RxPermissions.getInstance(StartActivity.this)
                .request(
                        Manifest.permission.READ_CALENDAR,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.CHANGE_WIFI_MULTICAST_STATE,
                        Manifest.permission.CHANGE_WIFI_STATE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.CAMERA
                )//这里填写所需要的权限
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean) {//true表示获取权限成功（注意这里在android6.0以下默认为true）
                            Log.i("permissions", "：获取成功");
                        } else {
                            Log.i("permissions", "：获取失败");
                        }
                    }
                });

    }


    @Override
    public void showDescription(String description) {

    }

    /**
     * 获取 token
     *
     * @param ipToken token
     */
    @Override
    public void showToken(String ipToken) {
        try {
            PlApplication.ipToken = ipToken;
            getIpPresenter = new GetIpPresenterImpl(this);
            getIpPresenter.getIpTask(SystemUtils.getUniquePsuedoID());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取 token 或 ip 失败
     *
     * @param error 失败信息
     */
    @Override
    public void showError(String error) {
        ToastUtil.ToastShow(error);
    }

    @Override
    public void showIp(String ip) {
        //保存 ip
        SharedPreUtil.saveApiSp(this, ip, PlApplication.user.getMobile());
        PlApplication.BASE_SERVER_URL = "http://" + ip + ":9095";
        Log.i(TAG, "showIp: " + PlApplication.BASE_SERVER_URL);


        if (thread == null) {
            if (myThread == null) {
                myThread = new MyThread();
            }
            thread = new Thread(myThread);
        }

        thread.start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myThread = null;
        thread = null;
    }

    class MyThread implements Runnable {

        @Override
        public void run() {
            while (i > 0) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnJump.setText("" + i);
                        i--;
                    }
                });
                try {
                    thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(StartActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

}
