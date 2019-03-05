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
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.pl.prod.R;
import com.pl.prod.ui.CleanEditText;
import com.pl.prod.utils.HexUtil;
import com.pl.prod.utils.ToastUtil;
import com.pl.prod.utils.WifiAutoConnectManager;
import com.pl.prod.utils.netty.NettyClientFilter;
import com.pl.prod.utils.netty.NettyClientHandler;
import com.uuzuche.lib_zxing.activity.CodeUtils;

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
//    @BindView(R.id.btn_scan)
//    Button btnScan;

    private WifiAutoConnectManager.WifiCipherType type = WifiAutoConnectManager.WifiCipherType.WIFICIPHER_WPA;
    private String password = "solonic.cc";
    private WifiAutoConnectManager mWifiAutoConnectManager;


    private int REQUEST_CODE = 1000;


    public String host = "192.168.6.6";  //ip地址
    public int port = 6636;       //端口
    /// 通过nio方式来接收连接和处理连接
    private EventLoopGroup group = new NioEventLoopGroup();
    private Bootstrap b = new Bootstrap();
    private Channel ch;

    private static TcpThread tcpThread;


    //tcp 信息
    public byte[] tcpMsg = new byte[]{
            //A区头部
            (byte) 0xFF, (byte) 0x00,
            (byte) 0x00,
            (byte) 0x04,
            //B区消息
            (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x10,
            //地址  192.168.0.160    172.20.10.2
            (byte) 0x31, (byte) 0x37, (byte) 0x32, (byte) 0x2E,
            (byte) 0x32, (byte) 0x30, (byte) 0x2E,
            (byte) 0x31, (byte) 0x30, (byte) 0x2E,
            (byte) 0x32,
            //空字符
            (byte) 0x00,
            //端口  8080
            (byte) 0x38, (byte) 0x30, (byte) 0x38, (byte) 0x30
    };

    //wifi发送 Tenda_2644B0
    public byte[] wifiMsg = new byte[]{
            //A区头部
            (byte) 0xFF, (byte) 0x00,
            (byte) 0x00,
            (byte) 0x05,
            //B区消息
            (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x0C,
            //SSID   Tenda_2644B0  TP-LINK_3836 ban
            (byte) 0x62, (byte) 0x61, (byte) 0x6E,
            //空字符
            (byte) 0x00,
            //密码 11111111
            (byte) 0x31, (byte) 0x31, (byte) 0x31, (byte) 0x31, (byte) 0x31, (byte) 0x31, (byte) 0x31, (byte) 0x31
    };

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        ButterKnife.bind(this);

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mWifiAutoConnectManager = WifiAutoConnectManager.newInstance(wifiManager);
        if (tcpThread == null) {
            tcpThread = new TcpThread();
        }

        if (thread == null) {
            thread = new Thread(tcpThread);
        }
        thread.start();

    }

    @Override
    protected void onResume() {
        super.onResume();
        int c = 0xAA;
        for (int i = 3; i < tcpMsg.length; i++) {
            Integer or = Integer.parseInt(HexUtil.byteToHex(tcpMsg[i]), 16);
            c = c ^ or;
        }
        tcpMsg[2] = (byte) c;

        c = 0xAA;
        for (int i = 3; i < wifiMsg.length; i++) {
            Integer or = Integer.parseInt(HexUtil.byteToHex(wifiMsg[i]), 16);
            c = c ^ or;
        }

        wifiMsg[2] = (byte) c;


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

//        Intent intent = new Intent(AddProductActivity.this, WifiActivity.class);
//        Product product = list.get(position);
//        intent.putExtra("product", product);
//        startActivity(intent);

        Log.i(TAG, "wifiMsg");
//        TaskCenter.sharedCenter().send(wifiMsg);
        if (ch != null) {
            Log.i(TAG, "发送 wifiMsg" + NettyClientHandler.toHexString(wifiMsg));
            ByteBuf byteBuf = Unpooled.buffer();
            byteBuf.writeBytes(wifiMsg);
            ch.writeAndFlush(byteBuf);
        }

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
}
