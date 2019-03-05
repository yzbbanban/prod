package com.pl.prod.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.pl.prod.R;

import com.pl.prod.adapter.BaseRecyclerViewAdapter;
import com.pl.prod.adapter.WifiListAdapter;
import com.pl.prod.consts.AppContants;

import com.pl.prod.entity.WifiParam;
import com.pl.prod.utils.CollectionUtils;
import com.pl.prod.utils.WifiSupport;

import android.net.wifi.ScanResult;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WifiActivity extends BaseActivity {

    @BindView(R.id.rv_wifi)
    RecyclerView rvWifi;
    private WifiListAdapter adapter;
    private LinearLayoutManager lm;

    private List<WifiParam> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);
        ButterKnife.bind(this);
        initData();
        initListener();
        sortScaResult();
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
            setResult(222);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateCustomToolBar(Toolbar toolbar) {
        super.onCreateCustomToolBar(toolbar);
        tv_center.setText("WIFI选择");
        tv_right.setText("");
    }

    protected void initData() {
        adapter = new WifiListAdapter(this);

        adapter.setList(list);
        lm = new LinearLayoutManager(this);
        rvWifi.setLayoutManager(lm);
        rvWifi.setAdapter(adapter);
    }


    /**
     * 获取wifi列表然后将bean转成自己定义的WifiBean
     */
    public void sortScaResult() {
        List<ScanResult> scanResults = WifiSupport.noSameName(WifiSupport.getWifiScanResult(this));
        list.clear();
        if (!CollectionUtils.isNullOrEmpty(scanResults)) {
            for (int i = 0; i < scanResults.size(); i++) {
                WifiParam wifiParam = new WifiParam();
                wifiParam.setWifiName(scanResults.get(i).SSID);
                wifiParam.setState(AppContants.WIFI_STATE_UNCONNECT);   //只要获取都假设设置成未连接，真正的状态都通过广播来确定
                wifiParam.setCapabilities(scanResults.get(i).capabilities);
                wifiParam.setLevel(WifiSupport.getLevel(scanResults.get(i).level) + "");
                list.add(wifiParam);

                //排序
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void initListener() {

        adapter.buttonSetOnclick(new WifiListAdapter.ButtonInterface() {
            @Override
            public void onclick(View view, int position) {
                Intent intent = new Intent(WifiActivity.this, WifiSetActivity.class);
                intent.putExtra("wifiName", list.get(position).getWifiName());
                startActivityForResult(intent, 0);

            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_OK) {
            setResult(222);
            finish();
        }
    }
}

