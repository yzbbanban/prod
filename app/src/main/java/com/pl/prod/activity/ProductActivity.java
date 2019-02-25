package com.pl.prod.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.pl.prod.R;
import com.pl.prod.entity.Product;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProductActivity extends BaseActivity {

    @BindView(R.id.banner)
    Banner banner;
    @BindView(R.id.tv_prod_wifi_name)
    TextView tvProdWifiName;
    @BindView(R.id.btn_prod_set_wifi)
    Button btnProdSetWifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        ButterKnife.bind(this);
        //设置轮播
        setBanner();
    }

    @OnClick(R.id.btn_prod_set_wifi)
    public void prodSetWifi() {
        startActivity(new Intent(this, WifiActivity.class));
    }


    @Override
    public void onCreateCustomToolBar(Toolbar toolbar) {
        super.onCreateCustomToolBar(toolbar);
        tv_center.setText("设备");
        tv_right.setText("设置");
        tv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProductActivity.this, ProductSetActivity.class));
            }
        });
    }


    private void setBanner() {

        String[] images = new String[]{"http://www.uuabb.cn/qq.png", "http://www.uuabb.cn/q2.png", "http://www.uuabb.cn/qq.png", "http://www.uuabb.cn/q3.png"};
        String[] titles = new String[]{};
        int position = getIntent().getIntExtra("position", 0);
        switch (position) {
            case 0:
                break;
            case 1:
                //设置样式 显示圆形指示器
                banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
                break;
            case 2:
                //显示数字指示器
                banner.setBannerStyle(BannerConfig.NUM_INDICATOR);
                break;
            case 3:
                //显示数字指示器和标题
                banner.setBannerStyle(BannerConfig.NUM_INDICATOR_TITLE);
                //记得设置标题列表哦
                banner.setBannerTitle(titles);
                break;
            case 4:
                //显示圆形指示器和标题
                banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE);
                //记得设置标题列表哦
                banner.setBannerTitle(titles);
                break;
            case 5:
                //设置指示器居中（CIRCLE_INDICATOR或者CIRCLE_INDICATOR_TITLE模式下）
                banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
                banner.setIndicatorGravity(BannerConfig.CENTER);
                break;
        }
        banner.setImages(images);//可以选择设置图片网址，或者资源文件，默认用Glide加载
        banner.setOnBannerClickListener(new Banner.OnBannerClickListener() {//设置点击事件
            @Override
            public void OnBannerClick(View view, int position) {
//                ToastUtil.showShortToast("position: " + position);
            }
        });
    }
}
