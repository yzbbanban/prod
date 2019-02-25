package com.pl.prod.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.pl.prod.R;
import com.pl.prod.app.PlApplication;
import com.pl.prod.utils.NetWorkUtils;
import com.pl.prod.utils.ToastUtil;
import com.pl.prod.utils.ToolbarHelper;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by brander on 2017/9/20.
 */
public class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    public String TAG = this.getClass().getName();

    private ToolbarHelper mToolBarHelper;
    public Toolbar toolbar;
    public TextView tv_center;
    public TextView tv_right;
    private SweetAlertDialog pDialog;

    //初始化加载对话框
    protected void initView() {
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
    }

    protected boolean checkNetWork() {
        boolean isOk = true;
        if (!NetWorkUtils.isConnected(PlApplication.app)) {
            ToastUtil.ToastShow(getString(R.string.str_network_error));
            isOk = false;
        }
        return isOk;
    }

    protected void initWebView() {

    }

    protected void initData() {
    }

    protected void initListener() {
    }

    //显示加载对话框
    protected void showLoading() {
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#00a6ff"));
        pDialog.setTitleText(getString(R.string.str_please_waitting));
        pDialog.setCancelable(false);
        pDialog.show();
    }

    //隐藏对话框
    protected void hideLoading() {
        pDialog.dismiss();
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        PlApplication.app.addActivity(this);
        super.onCreate(savedInstanceState);
    }

    /**
     * 初始化标题栏
     *
     * @param layoutResID
     */
    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        mToolBarHelper = new ToolbarHelper(this, layoutResID);
        toolbar = mToolBarHelper.getToolBar();
        toolbar.setTitle("");
        tv_center = mToolBarHelper.getTvCenter();
        tv_right = mToolBarHelper.getTvRight();
        tv_right.setOnClickListener(this);
        //返回帧布局视图
        setContentView(mToolBarHelper.getContentView());
        setSupportActionBar(toolbar);//把toolbar设置到activity中
        onCreateCustomToolBar(toolbar);
    }

    /**
     * 设置标题栏
     *
     * @param toolbar
     */
    public void onCreateCustomToolBar(Toolbar toolbar) {
        //插入toolbar视图的内容的起始点与结束点
        toolbar.setContentInsetsRelative(0, 0);
    }

    /**
     * 设置标题栏返回键功能
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void logInfo(Class cls, String message) {
        Log.i(cls.getName(), message);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pDialog != null) {
            pDialog.dismiss();
        }
        //删除该活动
//        TmsApplication.app.exit();
    }
}
