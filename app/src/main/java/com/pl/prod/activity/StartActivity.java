package com.pl.prod.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.pl.prod.R;
import com.pl.prod.app.PlApplication;
import com.pl.prod.contract.IGetIpContract;
import com.pl.prod.contract.IIpTokenContract;
import com.pl.prod.presenter.GetIpPresenterImpl;
import com.pl.prod.presenter.GetIpTokenPresenterImpl;
import com.pl.prod.utils.SharedPreUtil;
import com.pl.prod.utils.SystemUtils;
import com.pl.prod.utils.ToastUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
    private int i = 5;


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
                ipTokenPresenter.getIpTokenTask(SystemUtils.getIMEI(this));
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
    @TargetApi(23)
    private void getPersimmions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<String>();
            /***
             * 定位权限为必须权限，用户如果禁止，则每次进入都会申请
             */
            // 定位精确位置
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            /*
             * 读写权限和电话状态权限非必要权限(建议授予)只会申请一次，用户同意或者禁止，只会弹一次
             */
            // 读写权限
            if (addPermission(permissions, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                permissionInfo += "Manifest.permission.WRITE_EXTERNAL_STORAGE Deny \n";
            }

            //wifi权限
            if (addPermission(permissions, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                permissionInfo += "Manifest.permission.ACCESS_COARSE_LOCATION Deny \n";
            }
            if (addPermission(permissions, Manifest.permission.ACCESS_FINE_LOCATION)) {
                permissionInfo += "Manifest.permission.ACCESS_FINE_LOCATION Deny \n";
            }
            // 读写权限
            if (addPermission(permissions, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissionInfo += "Manifest.permission.WRITE_EXTERNAL_STORAGE Deny \n";
            }
            // 读取电话状态权限
            if (addPermission(permissions, Manifest.permission.READ_PHONE_STATE)) {
                permissionInfo += "Manifest.permission.READ_PHONE_STATE Deny \n";
            }
            // 读取电话状态权限
            if (addPermission(permissions, Manifest.permission.CALL_PHONE)) {
                permissionInfo += "Manifest.permission.READ_PHONE_STATE Deny \n";
            }
            //照相权限
            if (addPermission(permissions, Manifest.permission.CAMERA)) {
                permissionInfo += "Manifest.permission.WRITE_EXTERNAL_STORAGE Deny \n";
            }

            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), SDK_PERMISSION_REQUEST);
            }
        }
    }

    @TargetApi(23)
    private boolean addPermission(ArrayList<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) { // 如果应用没有获得对应权限,则添加到列表中,准备批量申请
            if (shouldShowRequestPermissionRationale(permission)) {
                return true;
            } else {
                permissionsList.add(permission);
                return false;
            }

        } else {
            return true;
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

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
            getIpPresenter.getIpTask(SystemUtils.getIMEI(this));
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
        ToastUtil.showShortToast(error);
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
