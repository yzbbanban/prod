package com.pl.prod.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pl.prod.R;
import com.pl.prod.ui.CleanEditText;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddProductActivity extends BaseActivity {

    @BindView(R.id.et_prod_ssid)
    CleanEditText etProdSsid;
    @BindView(R.id.et_product_name)
    CleanEditText etProductName;
    @BindView(R.id.et_product_remark)
    CleanEditText etProductRemark;
    @BindView(R.id.btn_connect)
    Button btnConnect;
    @BindView(R.id.btn_bind)
    Button btnBind;
    @BindView(R.id.btn_set_wifi)
    Button btnSetWifi;
//    @BindView(R.id.btn_scan)
//    Button btnScan;

    private int REQUEST_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        ButterKnife.bind(this);
    }


    @OnClick(R.id.btn_connect)
    public void connect() {
        //产品连接
        Toast.makeText(this, "序列号验证完成", Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "产品连接完成", Toast.LENGTH_SHORT).show();
    }

//    @OnClick(R.id.btn_scan)
    public void scan() {
        Intent intent = new Intent(AddProductActivity.this, CaptureActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }

                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    Toast.makeText(this, "解析结果:" + result, Toast.LENGTH_LONG).show();
                    etProdSsid.setText(result);

                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(AddProductActivity.this, "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @OnClick(R.id.btn_bind)
    public void bind() {
        //绑定
        Toast.makeText(this, "绑定完成", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @OnClick(R.id.btn_set_wifi)
    public void setifi() {
        Intent intent = new Intent(AddProductActivity.this, WifiActivity.class);
//        Product product = list.get(position);
//        intent.putExtra("product", product);
        startActivity(intent);
    }

    @Override
    public void onCreateCustomToolBar(Toolbar toolbar) {
        super.onCreateCustomToolBar(toolbar);
        tv_center.setText("添加设备");
        tv_right.setText("");
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
            startActivity(new Intent(AddProductActivity.this, MainActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
