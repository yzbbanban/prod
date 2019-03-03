package com.pl.prod.utils.netty;

import android.util.Log;

import com.pl.prod.app.PlApplication;

import java.io.IOException;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class ClientServer {

    private static final String TAG = "ClientServer";

    public static String host = "127.0.0.1";  //ip地址
    public static int port = 6789;          //端口
    /// 通过nio方式来接收连接和处理连接
    private static EventLoopGroup group = new NioEventLoopGroup();
    private static Bootstrap b = new Bootstrap();
    public static Channel ch;


    /**
     * Netty创建全部都是实现自AbstractBootstrap。
     * 客户端的是Bootstrap，服务端的则是    ServerBootstrap。
     **/
    public static void start() throws InterruptedException, IOException {
        b.group(group);
        b.channel(NioSocketChannel.class);
        b.handler(new NettyClientFilter());
        // 连接服务端
        ch = b.connect(host, port).sync().channel();
        Log.i(TAG, "main: 客户端成功启动...");
    }

    public static void send() throws IOException {
        String str = "Hello Netty";
        ch.writeAndFlush(str + "\r\n");
        System.out.println("客户端发送数据:" + str);
    }

}
