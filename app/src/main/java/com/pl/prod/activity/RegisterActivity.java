package com.pl.prod.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.pl.prod.R;
import com.pl.prod.app.PlApplication;
import com.pl.prod.contract.IRegisterContract;
import com.pl.prod.contract.ISmsContract;
import com.pl.prod.entity.SmsMessage;
import com.pl.prod.entity.UserRegister;
import com.pl.prod.presenter.RegisterPresenterImpl;
import com.pl.prod.presenter.SmsPresenterImpl;
import com.pl.prod.ui.CleanEditText;
import com.pl.prod.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity implements IRegisterContract.View, ISmsContract.View {

    @BindView(R.id.btn_login_back)
    Button btnLoginBack;
    @BindView(R.id.et_register_username)
    CleanEditText etRegisterUsername;
    @BindView(R.id.et_register_password)
    CleanEditText etRegisterPassword;
    @BindView(R.id.et_register_password_confirm)
    CleanEditText etRegisterPasswordConfirm;
    @BindView(R.id.et_register_code)
    CleanEditText etRegisterCode;
    @BindView(R.id.btn_register_code)
    Button btnRegisterCode;
    @BindView(R.id.btn_register)
    Button btnRegister;
    private int i = 60;

    private MyThread myThread;
    private Thread thread;

    private IRegisterContract.Presenter registerPresenter;

    private ISmsContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        initView();
        initData();
    }

    private void initData() {

    }

    private void initView() {

    }

    @OnClick(R.id.btn_register_code)
    public void sendRegisterCode() {
        //发送验证码
        presenter = new SmsPresenterImpl(this);
        SmsMessage sms = new SmsMessage();
        String mobile = etRegisterUsername.getText().toString();
        if (mobile.length() == 0) {
            ToastUtil.showShortToast("请输入手机号码");
        } else {
            sms.setPhoneNumber(mobile);
            sms.setCountryCode("86");
            try {
                presenter.smsTask(sms, 1);
                btnRegisterCode.setEnabled(false);

                if (thread == null) {
                    if (myThread == null) {
                        myThread = new MyThread();
                    }
                    thread = new Thread(myThread);
                }
                thread.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @OnClick(R.id.btn_register)
    public void register() {
        //注册
        registerPresenter = new RegisterPresenterImpl(this);
        UserRegister userRegister = new UserRegister();
        String code = etRegisterCode.getText().toString();
        String mobile = etRegisterUsername.getText().toString();
        String password = etRegisterPassword.getText().toString();
        String confirmPassword = etRegisterPasswordConfirm.getText().toString();
        if (code.length() == 0) {
            ToastUtil.showShortToast("请输入验证码");
            return;
        }
        if (mobile.length() == 0) {
            ToastUtil.showShortToast("请输入验证码");
            return;
        }
        if (password.length() == 0) {
            ToastUtil.showShortToast("请输入验证码");
            return;
        }
        if (!password.equals(confirmPassword)) {
            ToastUtil.showShortToast("两次密码不一致");
            return;
        }
        userRegister.setCode(code);
        userRegister.setCountryCode("86");
        userRegister.setMobile(mobile);
        userRegister.setPassword(password);

        try {
            registerPresenter.registerTask(userRegister);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @OnClick(R.id.btn_login_back)
    public void loginBack() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void showDescription(String description) {
        ToastUtil.showShortToast(description);
    }

    @Override
    public void showError(String error) {
        ToastUtil.showShortToast(error);
    }


    class MyThread implements Runnable {

        @Override
        public void run() {
            while (i > 0) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnRegisterCode.setText(i + "s");
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
                    btnRegisterCode.setText("获取验证码");
                    btnRegisterCode.setEnabled(true);
                    i = 60;
                    thread = null;
                    myThread = null;
                }
            });
        }
    }
}
