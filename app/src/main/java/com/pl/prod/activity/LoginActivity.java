package com.pl.prod.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.pl.prod.R;
import com.pl.prod.app.PlApplication;
import com.pl.prod.contract.ILoginContract;
import com.pl.prod.contract.IUpdateContract;
import com.pl.prod.entity.User;
import com.pl.prod.entity.VersionRecord;
import com.pl.prod.presenter.LoginPresenterImpl;
import com.pl.prod.presenter.UpdatePresenterImpl;
import com.pl.prod.ui.CleanEditText;
import com.pl.prod.utils.SharedPreUtil;
import com.pl.prod.utils.SystemUtils;
import com.pl.prod.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * 登录
 */
public class LoginActivity extends AppCompatActivity implements ILoginContract.View,
        IUpdateContract.View {
    private static final String TAG = "LoginActivity";
    @BindView(R.id.et_login_username)
    CleanEditText etLoginUsername;
    @BindView(R.id.et_login_password)
    CleanEditText etLoginPassword;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.btn_register)
    Button btnRegister;
    @BindView(R.id.btn_forg_pwd)
    TextView btnForgPwd;

    private SweetAlertDialog pDialog;

    private String name;//用户名
    private String password;//密码
    private ILoginContract.Presenter presenter;

    private IUpdateContract.Presenter updatePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initData();
    }

    @OnClick(R.id.btn_login)
    public void login() {
//        showLoading();
        User user = new User();
        name = etLoginUsername.getText().toString().trim();
        password = etLoginPassword.getText().toString().trim();
        if ("".equals(name) || "".equals(password)) {
            ToastUtil.ToastShow(getString(R.string.please_input_user));
            hideLoading();
        } else {

            user.setMobile(name);
            user.setUsername(name);
            user.setPassword(password);
            user.setCountryCode("86");
            try {
                presenter.loginTask(user);
            } catch (Exception e) {
                ToastUtil.ToastShow(getString(R.string.login_error));
                hideLoading();
            }
        }
    }


    @OnClick(R.id.btn_register)
    public void register() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btn_forg_pwd)
    public void forgPwd() {
        Intent intent = new Intent(this, ForgetPwdActivity.class);
        startActivity(intent);
    }


    private void updateApk(final String updateUrl) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择:")
                .setMessage("有新版本是否更新")
                .setPositiveButton("更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        //下载
                        try {
                            Intent intent = new Intent();
                            intent.setAction("android.intent.action.VIEW");
                            Uri content_url = Uri.parse(updateUrl);
                            intent.setData(content_url);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * 初始化用户信息
     */
    private void initData() {
        updatePresenter = new UpdatePresenterImpl(this);
        //1为 android
        updatePresenter.updateTask(1);

        presenter = new LoginPresenterImpl(this);
//        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        SharedPreferences preferences = getSharedPreferences("user", this.MODE_PRIVATE);
        String n = preferences.getString("name", "");
        String pwd = preferences.getString("password", "");

        if (!"".equals(n) && !"".equals(pwd)) {//存储在 sp 中的信息的非空判断
            etLoginUsername.setText(n);
            etLoginPassword.setText(pwd);
        }
    }

    //显示加载条
    protected void showLoading() {
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#00a6ff"));
        pDialog.setTitleText(getString(R.string.str_please_waitting));
        pDialog.setCancelable(false);
        pDialog.show();
    }

    //隐藏加载条
    protected void hideLoading() {
        pDialog.dismiss();
    }

    /**
     * 显示登陆完成信息
     *
     * @param description
     */
    @Override
    public void showDescription(String description) {
//        hideLoading();

    }

    @Override
    public void showError(String error) {
        ToastUtil.ToastShow(error);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pDialog != null) {
            pDialog.dismiss();
        }
    }


    @Override
    public void showLogin(String msg) {
        Log.i(TAG, "msg: " + msg);
        ToastUtil.ToastShow(getString(R.string.login_success));
        SharedPreUtil.saveUserSp(this, name, password);
        PlApplication.userToken = msg;
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void showUpdateMsg(VersionRecord msg) {
        String version = SystemUtils.getLocalVersionName(this);
        if (msg.getVersion().contains(version)) {
            updateApk(msg.getUrl());
        }

    }
}
