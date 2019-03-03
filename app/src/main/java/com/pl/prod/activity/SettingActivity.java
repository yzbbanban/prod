package com.pl.prod.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.pl.prod.R;
import com.pl.prod.app.PlApplication;
import com.pl.prod.utils.HexUtil;
import com.pl.prod.utils.TaskCenter;
import com.pl.prod.utils.TcpHelper;
import com.pl.prod.utils.netty.NettyClientFilter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class SettingActivity extends AppCompatActivity {

    private static final String TAG = "SettingActivity";

    @BindView(R.id.et_ip)
    EditText etIp;
    @BindView(R.id.et_port)
    EditText etPort;
    @BindView(R.id.et_message)
    EditText etMessage;
    @BindView(R.id.btn_connect)
    Button btnConnect;
    @BindView(R.id.tv_message)
    TextView tvMessage;
    @BindView(R.id.btn_connect_wifi)
    Button btnConnectWifi;
    @BindView(R.id.btn_connect_bind)
    Button btnConnectBind;
    @BindView(R.id.btn_connect_close)
    Button btnConnectClose;

    private TcpHelper tcpHelper;
    private String serIp;
    private int serPort;
    private String tcpRecData;

    //tcp 信息
    public byte[] tcpMsg = new byte[]{
            //A区头部
            (byte) 0xFF, (byte) 0x00,
            (byte) 0x00,
            (byte) 0x04,
            //B区消息
            (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x12,
            //地址  192.168.0.160
            (byte) 0x31, (byte) 0x39, (byte) 0x32, (byte) 0x2E,
            (byte) 0x31, (byte) 0x36, (byte) 0x38, (byte) 0x2E,
            (byte) 0x30, (byte) 0x2,
            (byte) 0x31, (byte) 0x36, (byte) 0x30,
            //空字符
            (byte) 0x00,
            //端口 9654 9090
            (byte) 0x39, (byte) 0x36, (byte) 0x35, (byte) 0x34
    };

    //wifi发送 Tenda_2644B0
    public byte[] wifiMsg = new byte[]{
            //A区头部
            (byte) 0xFF, (byte) 0x00,
            (byte) 0x00,
            (byte) 0x05,
            //B区消息
            (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x12,
            //SSID   Tenda_2644B0  TP-LINK_3836 ban
            (byte) 0x74, (byte) 0x70, (byte) 0x2, (byte) 0x6C, (byte) 0x69, (byte) 0x6E, (byte) 0x6,
            (byte) 0x5F,
            (byte) 0x33, (byte) 0x38, (byte) 0x33, (byte) 0x36,
            //空字符
            (byte) 0x00,
            //密码 19890804
            (byte) 0x31, (byte) 0x39, (byte) 0x38, (byte) 0x39, (byte) 0x30, (byte) 0x38, (byte) 0x30, (byte) 0x34
    };

//    //SSID   Tenda_2644B0  TP-LINK_3836 ban
//            (byte) 0x54, (byte) 0x65, (byte) 0x6E, (byte) 0x64, (byte) 0x61,
//            (byte) 0x5F,
//            (byte) 0x32, (byte) 0x36, (byte) 0x34, (byte) 0x34, (byte) 0x42, (byte) 0x30,
//            //空字符
//            (byte) 0x00,
//            //密码 19910516
//            (byte) 0x31, (byte) 0x39, (byte) 0x39, (byte) 0x31, (byte) 0x30, (byte) 0x35, (byte) 0x31, (byte) 0x36


    //序列号 发送
    public byte[] sdMsg = new byte[]{
            //A区头部
            (byte) 0xFF, (byte) 0x00,
            (byte) 0xFA,
            (byte) 0x06,
            //B区消息
            (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x08,
            (byte) 0x00, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x0A, (byte) 0x65, (byte) 0x56, (byte) 0x6E
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);


        try {
            start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //连接
//        TaskCenter.ipAddress = "192.168.0.102";
//        TaskCenter.ipAddress = "192.168.0.158";
//        TaskCenter.port = 8080;
//        TaskCenter.sharedCenter().connect("192.168.0.158", 8080);
//        List<ScanResult> scanResults = WifiSupport.noSameName(WifiSupport.getWifiScanResult(this));
//        Log.i(TAG, "onCreate: " + new Gson().toJson(scanResults));

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


//        TaskCenter.sharedCenter().setDisconnectedCallback(new TaskCenter.OnServerDisconnectedCallbackBlock() {
//            @Override
//            public void callback(IOException e) {
//                Log.i(TAG, "callback: " + e.getMessage());
//            }
//        });
//        TaskCenter.sharedCenter().setConnectedCallback(new TaskCenter.OnServerConnectedCallbackBlock() {
//            @Override
//            public void callback() {
//                Log.i(TAG, "connect");
//            }
//        });
//        TaskCenter.sharedCenter().setReceivedCallback(new TaskCenter.OnReceiveCallbackBlock() {
//            @Override
//            public void callback(String receicedMessage) {
//                Log.i(TAG, "receiced callback  " + receicedMessage);
//            }
//        });
    }


    public static String host = "192.168.0.158";  //ip地址
    public static int port = 8080;       //端口
    /// 通过nio方式来接收连接和处理连接
    private static EventLoopGroup group = new NioEventLoopGroup();
    private static Bootstrap b = new Bootstrap();
    private static Channel ch;


    /**
     * Netty创建全部都是实现自AbstractBootstrap。
     * 客户端的是Bootstrap，服务端的则是    ServerBootstrap。
     **/
    public void start() throws InterruptedException {
        b.group(group);
        b.channel(NioSocketChannel.class);
        b.handler(new NettyClientFilter());
        // 连接服务端
        ch = b.connect(host, port).sync().channel();

        Log.i(TAG, "main: 客户端成功启动...");
    }

    @OnClick(R.id.btn_connect)
    public void onSendMsgClick() {
//        serIp = etIp.getText().toString();
//        serPort = Integer.parseInt(etPort.getText().toString());
//        sendMsg("run tcp: ", tcpMsg);

        //发送
        Log.i(TAG, "tcpMsg");
//        TaskCenter.sharedCenter().send("ban".getBytes());
        if (ch != null) {
            ch.writeAndFlush(tcpMsg);
        }
    }

    @OnClick(R.id.btn_connect_wifi)
    public void onSendWifiMsgClick() {
//        serIp = etIp.getText().toString();
//        serPort = Integer.parseInt(etPort.getText().toString());
//        sendMsg("run wifi: ", wifiMsg);
        Log.i(TAG, "wifiMsg");
//        TaskCenter.sharedCenter().send(wifiMsg);
        if (ch != null) {
            ch.writeAndFlush(wifiMsg);
        }
    }

    @OnClick(R.id.btn_connect_bind)
    public void onSendBindMsgClick() {
//        serIp = etIp.getText().toString();
//        serPort = Integer.parseInt(etPort.getText().toString());
//        sendMsg("run bind: ", bindMsg);
        Log.i(TAG, "bindMsg");//00,01,01,01,0a,65,56,6e
//        TaskCenter.sharedCenter().send(bindMsg);
        if (ch != null) {
            ch.writeAndFlush(bindMsg);
        }
    }

    @OnClick(R.id.btn_connect_close)
    public void onSendCloseMsgClick() {
//        serIp = etIp.getText().toString();
//        serPort = Integer.parseInt(etPort.getText().toString());
//        sendMsg("run close: ", closeMsg);
        Log.i(TAG, "closeMsg");
//        TaskCenter.sharedCenter().send(closeMsg);
        if (ch != null) {
            ch.writeAndFlush(closeMsg);
        }
    }


    Socket socket = null;


    private void sendMsg(final String log, final byte[] msg) {

        final Thread socketthread = new Thread(new Runnable() {
            @Override
            public void run() {
                //创建socket
                try {
                    if (socket == null) {
                        socket = new Socket("192.168.6.6", 6636);//ip+端口号
                    }
                    Log.i(TAG, "socket连接成功" + log);

                    //向服务器端发送消息
                    OutputStream socketWriter = socket.getOutputStream();
                    System.out.println("开始发送" + log);

                    socketWriter.write(msg);
                    socketWriter.flush();
                    System.out.println("发送完毕，开始接收信息" + log);

                    //接收来自服务器端的消息
                    InputStream socketReader = socket.getInputStream();
                    byte strRxBuf[] = new byte[18];
                    int len = socketReader.read(strRxBuf, 0, 18);


                    if (len > 0) {
                        Log.i(TAG, log + " len: " + len);
                        for (int i = 0; i < strRxBuf.length; i++) {
                            Log.i(TAG, log + HexUtil.byteToHex(strRxBuf[i]));
                        }
                        Log.i(TAG, log + " 发送成功");
//                        isSuccessful = true;
                    } else {
                        Log.i(TAG, log + " 没有收到消息");
//                        isSuccessful = false;
                    }

                    //关闭流
                    socketWriter.close();
                    socketReader.close();
                    //关闭Socket
                    socket.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        socketthread.start();
    }


    @Override
    protected void onStop() {
        super.onStop();
        group = null;
        ch = null;
    }
}
