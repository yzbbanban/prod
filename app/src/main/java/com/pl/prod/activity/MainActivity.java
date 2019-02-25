package com.pl.prod.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;

import com.pl.prod.R;
import com.pl.prod.adapter.ProductListAdapter;
import com.pl.prod.entity.Product;
import com.pl.prod.utils.ToastUtil;

import java.util.List;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    @BindView(R.id.rv_product)
    RecyclerView rvProduct;//详细
    private ProductListAdapter adapter;
    private LinearLayoutManager lm;

    private List<Product> list = new ArrayList<>();

    private int closeCount = 0;

    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initData();
        initListener();
    }


    @Override
    public void onCreateCustomToolBar(Toolbar toolbar) {
        super.onCreateCustomToolBar(toolbar);
        tv_center.setText("主页");
        tv_right.setText("添加");
        tv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddProductActivity.class));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            closeCount++;
            if (closeCount >= 2 && System.currentTimeMillis() - startTime <= 1000) {
                finish();
                closeCount = 0;
                return true;
            } else {
                ToastUtil.ToastShow("请连续点击2次退出");
                startTime = System.currentTimeMillis();
                return false;
            }
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            closeCount++;
            if (closeCount >= 2 && System.currentTimeMillis() - startTime <= 1000) {
                finish();
                closeCount = 0;
                return true;
            } else {
                ToastUtil.ToastShow("请连续点击2次退出");
                startTime = System.currentTimeMillis();
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void initData() {
        adapter = new ProductListAdapter(this);
        for (int i = 0; i < 10; i++) {
            Product product = new Product();

            product.setCreateTime(System.currentTimeMillis() / 1000);
            product.setId(i + 1);
            product.setImageUrl("xaaaa");
            product.setRemark("xxxx" + i);
            product.setSsid("000" + i);
            product.setTypeId(1);
            product.setUpdateTime(System.currentTimeMillis() / 1000);
            product.setUserId(1);
            list.add(product);
        }

        adapter.setList(list);
        lm = new LinearLayoutManager(this);
        rvProduct.setLayoutManager(lm);
        rvProduct.setAdapter(adapter);
    }


    @Override
    protected void initListener() {
        adapter.buttonSetOnclick(new ProductListAdapter.ButtonInterface() {
            @Override
            public void onclick(View view, int position) {

            }
        });
        adapter.buttonIvSetOnclick(new ProductListAdapter.ButtonIvInterface() {
            @Override
            public void onclick(View view, int position) {
                Intent intent = new Intent(MainActivity.this, ProductActivity.class);
                Product product = list.get(position);
                intent.putExtra("product", product);
                startActivity(intent);
            }
        });
    }
}
