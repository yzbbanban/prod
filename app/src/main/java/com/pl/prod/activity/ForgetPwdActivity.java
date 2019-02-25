package com.pl.prod.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;

import com.pl.prod.R;
import com.pl.prod.contract.IFindPwdContract;
import com.pl.prod.contract.ISmsContract;
import com.pl.prod.entity.SmsMessage;
import com.pl.prod.entity.UserModify;
import com.pl.prod.presenter.FindPwdPresenterImpl;
import com.pl.prod.presenter.SmsPresenterImpl;
import com.pl.prod.ui.CleanEditText;
import com.pl.prod.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ForgetPwdActivity extends BaseActivity implements ISmsContract.View, IFindPwdContract.View {

    @BindView(R.id.et_forget_username)
    CleanEditText etForgetUsername;
    @BindView(R.id.et_forget_password)
    CleanEditText etForgetPassword;
    @BindView(R.id.et_forget_code)
    CleanEditText etForgetCode;
    @BindView(R.id.btn_forget_code)
    Button btnForgetCode;
    @BindView(R.id.btn_forget_pwd)
    Button btnForgetPwd;

    private ISmsContract.Presenter smsPresenter;

    private IFindPwdContract.Presenter pwdPresenter;

    private int i = 60;
    private MyThread myThread;
    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pwd);
        ButterKnife.bind(this);
    }


    @OnClick(R.id.btn_forget_pwd)
    public void confirmPwd() {
        String code = etForgetCode.getText().toString();
        if (code.length() != 0) {
            pwdPresenter = new FindPwdPresenterImpl(this);
            UserModify userModify = new UserModify();

            String username = etForgetUsername.getText().toString();
            String pwd = etForgetPassword.getText().toString();
            if (pwd.length() == 0) {
                ToastUtil.showShortToast("请填写密码");
                return;
            }
            if (username.length() == 0) {
                ToastUtil.showShortToast("请填写账户");
                return;
            }
            userModify.setCode(code);
            userModify.setMobile(username);
            userModify.setPassword(pwd);
            userModify.setCountryCode("86");
            try {
                pwdPresenter.findPwd(userModify);
            } catch (Exception e) {
                e.printStackTrace();
            }


        } else {
            ToastUtil.showShortToast("请输入验证码");
        }
    }

    @OnClick(R.id.btn_forget_code)
    public void sendForgetPwdSms() {
        SmsMessage sms = new SmsMessage();
        String mobile = etForgetUsername.getText().toString();
        if (mobile.length() == 0) {
            ToastUtil.showShortToast("请输入手机号");
        } else {
            sms.setPhoneNumber(mobile);
            sms.setCountryCode("86");
            try {
                smsPresenter = new SmsPresenterImpl(this);
                smsPresenter.smsTask(sms, 2);

                btnForgetCode.setEnabled(false);

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

    @Override
    public void onCreateCustomToolBar(Toolbar toolbar) {
        super.onCreateCustomToolBar(toolbar);
        tv_center.setText("忘记密码");
        tv_right.setText("");
    }

    @Override
    public void showDescription(String description) {
        ToastUtil.showShortToast("发送成功");
    }

    @Override
    public void showError(String error) {
        ToastUtil.showShortToast(error);
    }

    @Override
    public void showFindPwd(String msg) {
        ToastUtil.showShortToast(msg);
        finish();
    }

    class MyThread implements Runnable {

        @Override
        public void run() {
            while (i > 0) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnForgetCode.setText(i + "s");
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
                    btnForgetCode.setText("获取验证码");
                    btnForgetCode.setEnabled(true);
                    i = 60;
                    thread = null;
                    myThread = null;
                }
            });
        }
    }
}
