package com.pl.prod.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.pl.prod.R;
import com.pl.prod.app.PlApplication;
import com.pl.prod.consts.AppContants;
import com.pl.prod.entity.WifiParam;
import com.pl.prod.ui.CleanEditText;
import com.pl.prod.utils.CollectionUtils;
import com.pl.prod.utils.HexUtil;
import com.pl.prod.utils.ToastUtil;
import com.pl.prod.utils.WifiAutoConnectManager;
import com.pl.prod.utils.WifiSupport;
import com.pl.prod.utils.netty.NettyClientFilter;
import com.pl.prod.utils.netty.NettyClientHandler;

import java.util.*;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import static com.pl.prod.utils.WifiAutoConnectManager.wifiManager;

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
    @BindView(R.id.btn_connect_wifi)
    Button btnConnectWifi;
    @BindView(R.id.btn_close)
    Button btnClose;
    @BindView(R.id.tv_wifi_name)
    TextView tvWifiName;
    @BindView(R.id.btn_select_wifi)
    Button btnSelectWifi;
//    @BindView(R.id.btn_scan)
//    Button btnScan;

    private WifiAutoConnectManager.WifiCipherType type = WifiAutoConnectManager.WifiCipherType.WIFICIPHER_WPA;
    private String password = "solonic.cc";
    private WifiAutoConnectManager mWifiAutoConnectManager;
    private ConnectAsyncTask mConnectAsyncTask = null;
    private String ssid = "IBracket";

    private int REQUEST_CODE = 1000;


    public String host = "192.168.6.6";  //ip地址
    public int port = 6636;       //端口
    /// 通过nio方式来接收连接和处理连接
    private EventLoopGroup group = new NioEventLoopGroup();
    private Bootstrap b = new Bootstrap();
    private Channel ch;

    private static TcpThread tcpThread;

    private List<WifiParam> list = new ArrayList<>();
    //tcp 信息
    public byte[] tcpMsg;
//            = new byte[]{
//            //A区头部
//            (byte) 0xFF, (byte) 0x00,
//            (byte) 0x00,
//            (byte) 0x04,
//            //B区消息
//            (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x10,
//            //地址  192.168.0.160    172.20.10.2
//            (byte) 0x31, (byte) 0x37, (byte) 0x32, (byte) 0x2E,
//            (byte) 0x32, (byte) 0x30, (byte) 0x2E,
//            (byte) 0x31, (byte) 0x30, (byte) 0x2E,
//            (byte) 0x32,
//            //空字符
//            (byte) 0x00,
//            //端口  9995
//            (byte) 0x39, (byte) 0x39, (byte) 0x39, (byte) 0x35
//               ff 00 00 04 00 01 00 01 00 19 33 39 2e 31 30 38 2e 31 30 33 2e 31 32 38 00 39 39 39 35
//    };

    //wifi发送 Tenda_2644B0
    public byte[] wifiMsg;
//            = new byte[]{
//            //A区头部
//            (byte) 0xFF, (byte) 0x00,
//            (byte) 0x00,
//            (byte) 0x05,
//            //B区消息
//            (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x0C,
//            //SSID   Tenda_2644B0  TP-LINK_3836 ban
//            (byte) 0x62, (byte) 0x61, (byte) 0x6E,
//            //空字符
//            (byte) 0x00,
//            //密码 11111111
//            (byte) 0x31, (byte) 0x31, (byte) 0x31, (byte) 0x31, (byte) 0x31, (byte) 0x31, (byte) 0x31, (byte) 0x31
//    };
//          ff 00 00 05 00 01 00 01 00 0C 62 61 6E 00 31 31 31 31 31 31 31 31 31

    //绑定需求
    public byte[] bindMsg = new byte[]{
            //A区头部
            (byte) 0xFF, (byte) 0x00,
            (byte) 0xAC,
            (byte) 0x06,
    };


    //断开连接
    public byte[] closeMsg = new byte[]{
            //A区头部
            (byte) 0xFF, (byte) 0x00,
            (byte) 0xAD,
            (byte) 0x07
    };


    private static Thread thread;

    int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 1212;

    private void registerPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
        } else {
            getWifiList();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION) {
            getWifiList();
        }
    }

    public List<ScanResult> getWifiList() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        List<ScanResult> scanWifiList = wifiManager.getScanResults();
        List<ScanResult> wifiList = new ArrayList<>();
        if (scanWifiList != null && scanWifiList.size() > 0) {
            HashMap<String, Integer> signalStrength = new HashMap<String, Integer>();
            for (int i = 0; i < scanWifiList.size(); i++) {
                ScanResult scanResult = scanWifiList.get(i);
                if (!scanResult.SSID.isEmpty()) {
                    String key = scanResult.SSID + " " + scanResult.capabilities;
                    if (!signalStrength.containsKey(key)) {
                        signalStrength.put(key, i);
                        wifiList.add(scanResult);
                    }
                }
            }
        }
        Toast.makeText(this, "----> " + wifiList, Toast.LENGTH_LONG).show();
        return wifiList;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        ButterKnife.bind(this);
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mWifiAutoConnectManager = WifiAutoConnectManager.newInstance(wifiManager);
        sortScaResult();

        registerPermission();

//        getWifiList();

        boolean isNear = false;
        for (int i = 0; i < list.size(); i++) {
            WifiParam wifiInfo = list.get(i);
            if (ssid.equals(wifiInfo.getWifiName())) {
                ToastUtil.ToastShow("附近有产品，执行自动接入");
                isNear = true;

                if (ssid.equals(WifiAutoConnectManager.getSSID())) {
                    //已连接`
                    ToastUtil.ToastShow("附近有产品，已经接入产品 wifi");
                    break;
                }
                if (mConnectAsyncTask != null) {
                    mConnectAsyncTask.cancel(true);
                    mConnectAsyncTask = null;
                }
                mConnectAsyncTask = new ConnectAsyncTask(ssid, password, type);
                mConnectAsyncTask.execute();

                Thread thread = new Thread(new MyThread());
                thread.start();
                break;
            }
        }

        if (!isNear) {
            ToastUtil.ToastShow("附近无产品的 wifi");
        }


        if (tcpThread == null) {
            tcpThread = new TcpThread();
        }

        if (thread == null) {
            thread = new Thread(tcpThread);
        }
        thread.start();

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

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


        Log.i(TAG, "onResume: " + PlApplication.BASE_SERVER_URL);
        if (PlApplication.WIFI.length() != 0) {
            tvWifiName.setText("wifi: " + PlApplication.WIFI);
        }

        try {
            String url = PlApplication.BASE_SERVER_URL.split("//")[1];
            String u = url.split(":")[0];
            Log.i(TAG, "onResume: " + u);

            int tcpLength = 15 + u.length();
            int bodyLength = u.length();
            tcpMsg = new byte[tcpLength];

            for (int i = 0; i < tcpLength; i++) {
                if (i < 9) {
                    tcpMsg[0] = (byte) 0xFF;
                    tcpMsg[1] = (byte) 0x00;
                    tcpMsg[2] = (byte) 0x00;
                    tcpMsg[3] = (byte) 0x05;
                    tcpMsg[4] = (byte) 0x00;
                    tcpMsg[5] = (byte) 0x01;
                    tcpMsg[6] = (byte) 0x00;
                    tcpMsg[7] = (byte) 0x01;
                    tcpMsg[8] = (byte) 0x00;
                } else if (i == 9) {
                    Integer or = Integer.parseInt(String.valueOf(tcpLength - 10));
                    tcpMsg[9] = or.byteValue();
                } else if (i < 10 + bodyLength) {
                    tcpMsg[i] = (byte) u.charAt(i - 10);
                } else {
                    tcpMsg[tcpLength - 5] = (byte) 0x00;
                    tcpMsg[tcpLength - 4] = (byte) 0x39;
                    tcpMsg[tcpLength - 3] = (byte) 0x39;
                    tcpMsg[tcpLength - 2] = (byte) 0x39;
                    tcpMsg[tcpLength - 1] = (byte) 0x35;
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        int c = 0xAA;
        for (int i = 3; i < tcpMsg.length; i++) {
            Integer or = Integer.parseInt(HexUtil.byteToHex(tcpMsg[i]), 16);
            c = c ^ or;
        }
        tcpMsg[2] = (byte) c;


    }


    //    @OnClick(R.id.btn_set_wifi)
//    public void scan() {
//        Intent intent = new Intent(AddProductActivity.this, CaptureActivity.class);
//        startActivityForResult(intent, REQUEST_CODE);
//    }
    @OnClick(R.id.btn_connect_wifi)
    public void btnConnectWifi() {
        if (!"IBracket".equals(WifiAutoConnectManager.getSSID())) {
            Toast.makeText(this, "请连接名为 IBracket 的 wifi 密码为：" + password, Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "检测已连接产品wifi", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.btn_connect)
    public void connect() {
        //连接产品的 tcp
//        Toast.makeText(this, "序列号验证完成", Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, "产品连接完成", Toast.LENGTH_SHORT).show();
        //发送
        Log.i(TAG, "tcpMsg");

//        TaskCenter.sharedCenter().send("ban".getBytes());
        if (ch != null) {
            Log.i(TAG, "发送tcpMsg" + NettyClientHandler.toHexString(tcpMsg));

            ByteBuf byteBuf = Unpooled.buffer();
            byteBuf.writeBytes(tcpMsg);
            ch.writeAndFlush(byteBuf);
        }

    }

    @OnClick(R.id.btn_set_wifi)
    public void setWifi() {

        String u1 = PlApplication.WIFI;
        String s = PlApplication.WIFI_SECRET;
        tvWifiName.setText("wifi: " + u1);

        int wifiLength = 11 + u1.length() + s.length();
        wifiMsg = new byte[wifiLength];

        for (int i = 0; i < wifiLength; i++) {
            if (i < 9) {
                wifiMsg[0] = (byte) 0xFF;
                wifiMsg[1] = (byte) 0x00;
                wifiMsg[2] = (byte) 0x00;
                wifiMsg[3] = (byte) 0x05;
                wifiMsg[4] = (byte) 0x00;
                wifiMsg[5] = (byte) 0x01;
                wifiMsg[6] = (byte) 0x00;
                wifiMsg[7] = (byte) 0x01;
                wifiMsg[8] = (byte) 0x00;
            } else if (i == 9) {
                Integer or = Integer.parseInt(String.valueOf(wifiLength - 10));
                wifiMsg[9] = or.byteValue();
            } else if (i < 10 + u1.length()) {
                wifiMsg[i] = (byte) u1.charAt(i - 10);
            } else if (i == u1.length() + 10) {
                wifiMsg[i] = (byte) 0x00;
            } else {
                wifiMsg[i] = (byte) s.charAt(wifiLength - i - 1);
            }

        }

        int c = 0xAA;
        for (int i = 3; i < wifiMsg.length; i++) {
            Integer or = Integer.parseInt(HexUtil.byteToHex(wifiMsg[i]), 16);
            c = c ^ or;
        }

        wifiMsg[2] = (byte) c;


        Log.i(TAG, "wifiMsg");
        if (ch != null) {
            Log.i(TAG, "发送 wifiMsg" + NettyClientHandler.toHexString(wifiMsg));
            ByteBuf byteBuf = Unpooled.buffer();
            byteBuf.writeBytes(wifiMsg);
            ch.writeAndFlush(byteBuf);
        }


    }

    @OnClick(R.id.btn_select_wifi)
    public void selectWifi() {

        Intent intent = new Intent(AddProductActivity.this, WifiActivity.class);
        startActivityForResult(intent, REQUEST_CODE);


    }

    @OnClick(R.id.btn_bind)
    public void bind() {
        //绑定
        Log.i(TAG, "bindMsg");//00,01,01,01,0a,65,56,6e
//        TaskCenter.sharedCenter().send(bindMsg);
        if (ch != null) {
            ByteBuf byteBuf = Unpooled.buffer();
            byteBuf.writeBytes(bindMsg);
            ch.writeAndFlush(byteBuf);
        }
    }

    @OnClick(R.id.btn_close)
    public void btnClose() {
        //绑定
        Log.i(TAG, "closeMsg");
//        TaskCenter.sharedCenter().send(closeMsg);
        if (ch != null) {
            Log.i(TAG, "发送 closeMsg");
            ByteBuf byteBuf = Unpooled.buffer();
            byteBuf.writeBytes(closeMsg);
            ch.writeAndFlush(byteBuf);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

//            if (null != data) {
//                Bundle bundle = data.getExtras();
//                if (bundle == null) {
//                    return;
//                }

//                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {

//                    String result = bundle.getString(CodeUtils.RESULT_STRING);
//                    Toast.makeText(this, "解析结果:" + result, Toast.LENGTH_LONG).show();
//                    etProdSsid.setText(result);


            Log.i(TAG, "onActivityResult: " + PlApplication.WIFI + " : " + PlApplication.WIFI_SECRET);

            String u1 = PlApplication.WIFI;
            String s = PlApplication.WIFI_SECRET;
            tvWifiName.setText("wifi: " + u1);

            int wifiLength = 11 + u1.length() + s.length();
            wifiMsg = new byte[wifiLength];

            for (int i = 0; i < wifiLength; i++) {
                if (i < 9) {
                    wifiMsg[0] = (byte) 0xFF;
                    wifiMsg[1] = (byte) 0x00;
                    wifiMsg[2] = (byte) 0x00;
                    wifiMsg[3] = (byte) 0x05;
                    wifiMsg[4] = (byte) 0x00;
                    wifiMsg[5] = (byte) 0x01;
                    wifiMsg[6] = (byte) 0x00;
                    wifiMsg[7] = (byte) 0x01;
                    wifiMsg[8] = (byte) 0x00;
                } else if (i == 9) {
                    Integer or = Integer.parseInt(String.valueOf(wifiLength - 10));
                    wifiMsg[9] = or.byteValue();
                } else if (i < 10 + u1.length()) {
                    wifiMsg[i] = (byte) u1.charAt(i - 10);
                } else if (i == u1.length() + 10) {
                    wifiMsg[i] = (byte) 0x00;
                } else {
                    wifiMsg[i] = (byte) s.charAt(wifiLength - i - 1);
                }

            }

            int c = 0xAA;
            for (int i = 3; i < wifiMsg.length; i++) {
                Integer or = Integer.parseInt(HexUtil.byteToHex(wifiMsg[i]), 16);
                c = c ^ or;
            }

            wifiMsg[2] = (byte) c;


            Log.i(TAG, "wifiMsg");
            if (ch != null) {
                Log.i(TAG, "发送 wifiMsg" + NettyClientHandler.toHexString(wifiMsg));
                ByteBuf byteBuf = Unpooled.buffer();
                byteBuf.writeBytes(wifiMsg);
                ch.writeAndFlush(byteBuf);
            }

//                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
//                    Toast.makeText(AddProductActivity.this, "wifi获取失败", Toast.LENGTH_LONG).show();
//                }
//            }


        }

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


    /**
     * Netty创建全部都是实现自AbstractBootstrap。
     * 客户端的是Bootstrap，服务端的则是    ServerBootstrap。
     **/
    public void start() throws Exception {
        if (group == null) {
            group = new NioEventLoopGroup();
            b = new Bootstrap();
        }
        b.group(group);
        b.channel(NioSocketChannel.class);
        b.handler(new NettyClientFilter());
        // 连接服务端
        ch = b.connect(host, port).sync().channel();

        Log.i(TAG, "main: 客户端成功启动...");
    }

    class TcpThread implements Runnable {

        @Override
        public void run() {

            try {
                start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tcpThread = null;
        thread = null;
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
                                new AlertDialog.Builder(AddProductActivity.this)
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
}
