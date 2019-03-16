package com.pl.prod.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pl.prod.R;
import com.pl.prod.app.PlApplication;
import com.pl.prod.consts.AppContants;
import com.pl.prod.contract.IGetIpContract;
import com.pl.prod.contract.IIpTokenContract;
import com.pl.prod.contract.ILoginContract;
import com.pl.prod.contract.IUpdateContract;
import com.pl.prod.entity.User;
import com.pl.prod.entity.VersionRecord;
import com.pl.prod.entity.WifiParam;
import com.pl.prod.presenter.GetIpPresenterImpl;
import com.pl.prod.presenter.GetIpTokenPresenterImpl;
import com.pl.prod.presenter.LoginPresenterImpl;
import com.pl.prod.presenter.UpdatePresenterImpl;
import com.pl.prod.utils.AddProductUtil;
import com.pl.prod.utils.CollectionUtils;
import com.pl.prod.utils.DialogUtil;
import com.pl.prod.utils.HexUtil;
import com.pl.prod.utils.SharedPreUtil;
import com.pl.prod.utils.SystemUtils;
import com.pl.prod.utils.ToastUtil;
import com.pl.prod.utils.WifiAutoConnectManager;
import com.pl.prod.utils.WifiSupport;
import com.pl.prod.utils.netty.NettyClientFilter;
import com.pl.prod.utils.netty.NettyClientHandler;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import rx.functions.Action1;

import static com.pl.prod.utils.WifiAutoConnectManager.wifiManager;

public class WebActivity extends AppCompatActivity implements
        IGetIpContract.View,
        IIpTokenContract.View,
        ILoginContract.View,
        IUpdateContract.View {
    private static final String TAG = "NewRfidActivity";
    private WebView mWebView;
    private String loginStatus;
    private static AlertDialog alertDialog;
    private static AlertDialog.Builder builder;

    //----登录 更新------
    private ILoginContract.Presenter presenter;

    private IUpdateContract.Presenter updatePresenter;
    //------------

    //------------
    //获取 ip
    private IGetIpContract.Presenter getIpPresenter;
    private IIpTokenContract.Presenter ipTokenPresenter;
    //------------

    //------------wifi

    private List<WifiParam> list = new ArrayList<>();

    private String name = "";
    private String password = "";


    //------------
    private WifiAutoConnectManager.WifiCipherType type = WifiAutoConnectManager.WifiCipherType.WIFICIPHER_WPA;
    private String pwd = "solonic.cc";
    private WifiAutoConnectManager mWifiAutoConnectManager;
    private ConnectAsyncTask mConnectAsyncTask = null;
    private String ssid = "IBracket";
    public String host = "192.168.6.6";  //ip地址
    public int port = 6636;       //端口
    /// 通过nio方式来接收连接和处理连接
    private EventLoopGroup group = new NioEventLoopGroup();
    private Bootstrap b = new Bootstrap();
    private Channel ch;

    private static TcpThread tcpThread;

    private static Thread thread;
    private Gson gson = new Gson();


    //tcp 信息
    public byte[] tcpMsg;

    //wifi发送 Tenda_2644B0
    public byte[] wifiMsg;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPersimmions();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        updatePresenter = new UpdatePresenterImpl(this);
        presenter = new LoginPresenterImpl(this);
        getIpPresenter = new GetIpPresenterImpl(this);
        ipTokenPresenter = new GetIpTokenPresenterImpl(this);

    }


    private void sendTcp() {
        Log.i(TAG, "onResume: " + PlApplication.BASE_SERVER_URL);

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

    /**
     * 权限检查
     */
    private void getPersimmions() {


        //这个请求事件我写在点击事件里面，
        //点击button之后RxPermissions会为我们申请运行时权限
        RxPermissions.getInstance(WebActivity.this)
                .request(
                        Manifest.permission.READ_CALENDAR,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.CHANGE_WIFI_MULTICAST_STATE,
                        Manifest.permission.CHANGE_WIFI_STATE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.CAMERA
                )//这里填写所需要的权限
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean) {//true表示获取权限成功（注意这里在android6.0以下默认为true）
                            Log.i("permissions", "：获取成功");
                        } else {
                            Log.i("permissions", "：获取失败");
                        }
                    }
                });

    }


    private void initView() {
        mWebView.requestFocus();
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setAllowFileAccess(true);// 设置允许访问文件数据
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setDatabaseEnabled(true);
        //添加客户端支持
        mWebView.setWebChromeClient(new MyWebChromeClient());

        mWebView.loadUrl("file:///android_asset/login.html");
        //在js中调用本地java方法
        mWebView.addJavascriptInterface(new JsInterface(this), "AndroidWebView");
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url != "") {
                    view.loadUrl(url);   //在当前的webview中跳转到新的url
                    Log.i(TAG, "url:" + url);
                }
                return true;
            }
        });
        builder = new AlertDialog.Builder(this);
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:

                    break;
                case 1:
                    builder.setTitle("提示")
                            .setMessage((String) msg.obj)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();

                                }
                            })
                            .setNegativeButton("取消", null);
                    alertDialog = builder.create();
                    if (alertDialog.isShowing()) {
                        alertDialog.dismiss();
                    } else {
                        alertDialog.show();
                    }
                    break;
            }

        }
    };


    @Override
    public void showLogin(String msg) {
        Log.i(TAG, "msg: " + msg);
        ToastUtil.ToastShow(getString(R.string.login_success));
        SharedPreUtil.saveUserSp(this, name, password);
        PlApplication.userToken = msg;
        mWebView.loadUrl("javascript:sendLogin('" + name + "')");
    }

    @Override
    public void showUpdateMsg(VersionRecord msg) {
        String version = SystemUtils.getLocalVersionName(this);
        if (msg.getVersion().contains(version)) {
            DialogUtil.updateApk(msg.getUrl(), this);
        }

    }

    @Override
    public void showDescription(String description) {
        ToastUtil.ToastShow(description);
    }

    /**
     * 获取 token
     *
     * @param ipToken token
     */
    @Override
    public void showToken(String ipToken) {
        try {
            PlApplication.ipToken = ipToken;
            getIpPresenter.getIpTask(SystemUtils.getUniquePsuedoID());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showIp(String ip) {
        //保存 ip
        SharedPreUtil.saveApiSp(this, ip, PlApplication.user.getMobile());
        PlApplication.BASE_SERVER_URL = "http://" + ip + ":9095";
        Log.i(TAG, "showIp: " + PlApplication.BASE_SERVER_URL);
        sendTcp();
    }


    @Override
    public void showError(String error) {
        ToastUtil.ToastShow(error);
    }


    /**
     * js调用此方法
     */
    private class JsInterface {
        private Context mContext;

        public JsInterface(Context context) {
            this.mContext = context;
        }

        //在js中调用window.AndroidWebView.showInfoFromJs(name)，便会触发此方法。
        //登录
        @JavascriptInterface
        public void loginJs(String name, String password) {
            Toast.makeText(mContext, name + ":" + password, Toast.LENGTH_SHORT).show();
            login(name, password);
        }

        //更新 app
        @JavascriptInterface
        public void updateAppJs() {
            Toast.makeText(mContext, "updateApp", Toast.LENGTH_SHORT).show();
            getNewApp();
        }

        //获取 ip
        @JavascriptInterface
        public void getIpJs() {
            Toast.makeText(mContext, "getIP", Toast.LENGTH_SHORT).show();
            getIp();
        }

        //获取 设备一维码
        @JavascriptInterface
        public void getSsidJs() {
            mWebView.loadUrl("javascript:sendSsid('" + PlApplication.SSID + "')");
            Toast.makeText(mContext, "getSsidJs--> " + PlApplication.SSID, Toast.LENGTH_SHORT).show();
        }

        //开启 tcp 请求
        @JavascriptInterface
        public void startTcpJs() {
            Toast.makeText(mContext, "startTcpJs--> ", Toast.LENGTH_SHORT).show();
            linkTcp();
        }

        //获取 ip 列表
        @JavascriptInterface
        public void getWifiListJs() {
            Toast.makeText(mContext, "getWifiListJs--> ", Toast.LENGTH_SHORT).show();
            getWifiList();
        }

        //连接 IBacketJs
        @JavascriptInterface
        public void linkIBracketJs() {
            Toast.makeText(mContext, "linkIBracketJs--> ", Toast.LENGTH_SHORT).show();
            linkIBarcket();
        }

        //连接 tcp
        @JavascriptInterface
        public void connectTcpJs() {
            Toast.makeText(mContext, "connectTcp--> ", Toast.LENGTH_SHORT).show();
            connectTcp();
        }

        //发送 wifi 信息
        @JavascriptInterface
        public void sendWifiJs(String wifiName, String password) {
            Toast.makeText(mContext, "connectTcp--> ", Toast.LENGTH_SHORT).show();
            sendWifi(wifiName, password);
        }

        //绑定 wifi
        @JavascriptInterface
        public void bindJs() {
            Toast.makeText(mContext, "bindJs--> ", Toast.LENGTH_SHORT).show();
            bind();
        }

        //关闭
        @JavascriptInterface
        public void btnCloseJs() {
            Toast.makeText(mContext, "btnCloseJs--> ", Toast.LENGTH_SHORT).show();
            btnClose();
        }

    }

    class MyWebChromeClient extends WebChromeClient {

        /**
         * 处理加载进度
         *
         * @param view
         * @param newProgress
         */
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
        }

        /**
         * alert弹框
         *
         * @return
         */
        @Override
        public boolean onJsAlert(WebView view, String url, final String message, JsResult result) {
            Log.d("main", "onJsAlert:" + message);
            Message msg = new Message();
            msg.obj = message;
            msg.what = 1;
            handler.sendMessage(msg);

            result.confirm();//这里必须调用，否则页面会阻塞造成假死
            return true;
        }

        //设置响应js 的Confirm()函数
        @Override
        public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
            AlertDialog.Builder b = new AlertDialog.Builder(WebActivity.this);
            b.setTitle("提示");
            b.setMessage(message);
            b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    result.confirm();
                }
            });
            b.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    result.cancel();
                }
            });
            b.create().show();
            result.confirm();//这里必须调用，
            return true;
        }

    }


    //---------login---------
    public void login(String name, String password) {
        User user = new User();
        if ("".equals(name) || "".equals(password)) {
            ToastUtil.ToastShow(getString(R.string.please_input_user));
        } else {

            user.setMobile(name);
            user.setUsername(name);
            user.setPassword(password);
            user.setCountryCode("86");
            try {
                presenter.loginTask(user);
            } catch (Exception e) {
                ToastUtil.ToastShow(getString(R.string.login_error));
            }
        }
    }
    //-------------------

    //----------获取 wifi---------

    /**
     * 获取wifi列表然后将bean转成自己定义的WifiBean
     */
    public void getWifiList() {
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
        mWebView.loadUrl("javascript:sendWifiList('" + gson.toJson(list) + "')");
    }
    //---------------------

    //---------------------

    /**
     * 初始化数据，默认3秒开启 app
     */
    private void getIp() {

        //加载 ip 获取 ip token
        String ip = SharedPreUtil.getAPiSp(this, PlApplication.user.getMobile());
        if (ip == null || "".equals(ip) || ip.length() > 0) {
            try {
                ipTokenPresenter.getIpTokenTask(SystemUtils.getUniquePsuedoID());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            PlApplication.url = "http://" + ip + ":9095";
        }

        mWebView.loadUrl("javascript:sendIp('" + PlApplication.url + "')");
    }
    //---------------------


    //--------更新-----------

    /**
     * 初始化用户信息
     */
    private void getNewApp() {
        //1为 android
        updatePresenter.updateTask(1);

    }


    //---------连接 wifi----------

    /**
     * 初始化用户信息
     */
    private void linkIBarcket() {
        //1为 android
        AddProductUtil addProductUtil = new AddProductUtil(this);
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
                mConnectAsyncTask = new ConnectAsyncTask(ssid, pwd, type);
                mConnectAsyncTask.execute();

                Thread thread = new Thread(new MyThread());
                thread.start();
                break;
            }
        }

    }

    //---------连接启动 tcp----------

    private void linkTcp() {
        if (tcpThread == null) {
            tcpThread = new TcpThread();
        }

        if (thread == null) {
            thread = new Thread(tcpThread);
        }
        thread.start();
    }

    //-----------绑定 tcp------------
    public void connectTcp() {
        //连接产品的 tcp
        Log.i(TAG, "tcpMsg");
        if (ch != null) {
            Log.i(TAG, "发送tcpMsg" + NettyClientHandler.toHexString(tcpMsg));

            ByteBuf byteBuf = Unpooled.buffer();
            byteBuf.writeBytes(tcpMsg);
            ch.writeAndFlush(byteBuf);
        }

    }
    //-----------------------


    //---------连接 wifi----------
    private void sendWifi(String u1, String s) {
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
//                wifiMsg[i] = (byte) s.charAt(wifiLength - i - 1);
                wifiMsg[i] = (byte) s.charAt(s.length() + i - wifiLength);
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

    //------------绑定数据-------
    public void bind() {
        //绑定
        Log.i(TAG, "bindMsg");//00,01,01,01,0a,65,56,6e
        if (ch != null) {
            ByteBuf byteBuf = Unpooled.buffer();
            byteBuf.writeBytes(bindMsg);
            ch.writeAndFlush(byteBuf);
        }
    }
    //-------------------


    //------------关闭-------
    public void btnClose() {
        //绑定
        Log.i(TAG, "closeMsg");
        if (ch != null) {
            Log.i(TAG, "发送 closeMsg");
            ByteBuf byteBuf = Unpooled.buffer();
            byteBuf.writeBytes(closeMsg);
            ch.writeAndFlush(byteBuf);
        }
    }
    //-------------------


    //------------------
    //-------------------


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
                                new AlertDialog.Builder(WebActivity.this)
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
                        WifiConfiguration wifiConfig = mWifiAutoConnectManager.createWifiInfo(ssid, pwd,
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
                        WifiConfiguration wifiConfig = mWifiAutoConnectManager.createWifiInfo(ssid, pwd, type);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tcpThread = null;
        thread = null;
    }

}
