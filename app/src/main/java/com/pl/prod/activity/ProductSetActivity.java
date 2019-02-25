package com.pl.prod.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.TextView;

import com.pl.prod.R;
import com.pl.prod.ui.CleanEditText;
import com.pl.prod.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProductSetActivity extends BaseActivity {

    @BindView(R.id.et_prod_ssid)
    TextView etProdSsid;
    @BindView(R.id.et_product_name)
    CleanEditText etProductName;
    @BindView(R.id.et_product_remark)
    CleanEditText etProductRemark;
    @BindView(R.id.btn_set_wifi)
    Button btnSetWifi;
    @BindView(R.id.btn_set_bind)
    Button btnBind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_set);
        ButterKnife.bind(this);

    }


    @Override
    public void onCreateCustomToolBar(Toolbar toolbar) {
        super.onCreateCustomToolBar(toolbar);
        tv_center.setText("设备设备");
    }


    @OnClick(R.id.btn_set_bind)
    public void setBind() {
        ToastUtil.showLongToast("绑定设备成功");
        finish();
    }


    @OnClick(R.id.btn_set_wifi)
    public void setWifi() {
        startActivity(new Intent(this, WifiActivity.class));
    }

}
