package com.pl.prod.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pl.prod.R;
import com.pl.prod.ui.CleanEditText;
import com.pl.prod.utils.WifiAutoConnectManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.pl.prod.utils.WifiAutoConnectManager.wifiManager;

public class WifiSetActivity extends BaseActivity {

    @BindView(R.id.tv_wifi_name)
    TextView tvWifiName;
    @BindView(R.id.et_login_password)
    CleanEditText etLoginPassword;
    @BindView(R.id.btn_wifi_set_cancel)
    Button btnWifiSetCancel;
    @BindView(R.id.btn_wifi_set_confirm)
    Button btnWifiSetConfirm;

    private ConnectAsyncTask mConnectAsyncTask = null;
    private String ssid = "";
    private WifiAutoConnectManager.WifiCipherType type = WifiAutoConnectManager.WifiCipherType.WIFICIPHER_WPA;
    private String password = "solonic.cc";
    private WifiAutoConnectManager mWifiAutoConnectManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_set);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        String wifiName = intent.getStringExtra("wifiName");
        ssid = wifiName;
        tvWifiName.setText(wifiName);
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mWifiAutoConnectManager = WifiAutoConnectManager.newInstance(wifiManager);
    }


    @Override
    public void onCreateCustomToolBar(Toolbar toolbar) {
        super.onCreateCustomToolBar(toolbar);
        tv_center.setText("WIFI设置");
        tv_right.setText("");
    }


    @OnClick(R.id.btn_wifi_set_cancel)
    public void setCancel() {
        finish();
    }

    @OnClick(R.id.btn_wifi_set_confirm)
    public void setConfirm() {
        if (ssid.equals(WifiAutoConnectManager.getSSID())) {
            return;
        }
        if (mConnectAsyncTask != null) {
            mConnectAsyncTask.cancel(true);
            mConnectAsyncTask = null;
        }
        mConnectAsyncTask = new ConnectAsyncTask(ssid, password, type);
        mConnectAsyncTask.execute();

        Thread thread = new Thread(new MyThread());
        thread.start();

    }

    class MyThread implements Runnable {

        @Override
        public void run() {
            int i = 0;
            while (i < 15) {
                if (ssid.equals(WifiAutoConnectManager.getSSID())) {
                    finish();
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                i++;
            }
        }
    }


    class ConnectAsyncTask extends AsyncTask<Void, Void, Boolean> {
        private String ssid;
        private String password;
        private WifiAutoConnectManager.WifiCipherType type;
        WifiConfiguration tempConfig;

        public ConnectAsyncTask(String ssid, String password, WifiAutoConnectManager.WifiCipherType type) {
            this.ssid = ssid;
            this.password = password;
            this.type = type;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            // 打开wifi
            mWifiAutoConnectManager.openWifi();
            // 开启wifi功能需要一段时间(我在手机上测试一般需要1-3秒左右)，所以要等到wifi
            // 状态变成WIFI_STATE_ENABLED的时候才能执行下面的语句
            while (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
                try {
                    // 为了避免程序一直while循环，让它睡个100毫秒检测……
                    Thread.sleep(100);

                } catch (InterruptedException ie) {
                    Log.e("wifidemo", ie.toString());
                }
            }

            tempConfig = mWifiAutoConnectManager.isExsits(ssid);
            //禁掉所有wifi
//            for (WifiConfiguration c : wifiManager.getConfiguredNetworks()) {
//                wifiManager.disableNetwork(c.networkId);
//            }
            if (tempConfig != null) {
                Log.d("wifidemo", ssid + "配置过！");
                boolean result = wifiManager.enableNetwork(tempConfig.networkId, true);
                if (type == WifiAutoConnectManager.WifiCipherType.WIFICIPHER_NOPASS) {
                    try {
                        Thread.sleep(5000);//超过5s提示失败
                        Log.d("wifidemo", ssid + "连接失败！");
                        wifiManager.disableNetwork(tempConfig.networkId);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Toast.makeText(getApplicationContext(), "连接失败!请在系统里删除wifi连接，重新连接。", Toast.LENGTH_SHORT).show();
                                new AlertDialog.Builder(WifiSetActivity.this)
                                        .setTitle("连接失败！")
                                        .setMessage("请在系统里删除wifi连接，重新连接。")
                                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent();
                                                intent.setAction("android.net.wifi.PICK_WIFI_NETWORK");
                                                startActivity(intent);
                                            }
                                        }).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.d("wifidemo", "result=" + result);
                    return result;
                } else {
                    Log.d("wifidemo", ssid + "没有配置过！");
                    if (type == WifiAutoConnectManager.WifiCipherType.WIFICIPHER_WPA) {
                        WifiConfiguration wifiConfig = mWifiAutoConnectManager.createWifiInfo(ssid, password,
                                type);
                        if (wifiConfig == null) {
                            Log.d("wifidemo", "wifiConfig is null!");
                            return null;
                        }
                        Log.d("wifidemo", wifiConfig.SSID);

                        int netID = wifiManager.addNetwork(wifiConfig);
                        boolean enabled = wifiManager.enableNetwork(netID, true);
                        Log.d("wifidemo", "enableNetwork status enable=" + enabled);
//                                                    Log.d("wifidemo", "enableNetwork connected=" + mWifiAutoConnectManager.wifiManager.reconnect());
//                                                    mWifiAutoConnectManager.wifiManager.reconnect();
                    } else {
                        WifiConfiguration wifiConfig = mWifiAutoConnectManager.createWifiInfo(ssid, password, type);
                        if (wifiConfig == null) {
                            Log.d("wifidemo", "wifiConfig is null!");
                            return false;
                        }
                        Log.d("wifidemo", wifiConfig.SSID);
                        int netID = wifiManager.addNetwork(wifiConfig);
                        boolean enabled = wifiManager.enableNetwork(netID, true);
                        Log.d("wifidemo", "enableNetwork status enable=" + enabled);
//                    Log.d("wifidemo", "enableNetwork connected=" + mWifiAutoConnectManager.wifiManager.reconnect());
//                    return mWifiAutoConnectManager.wifiManager.reconnect();
                        return enabled;
                    }
                    return false;


                }

            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            mConnectAsyncTask = null;
        }
    }
}

