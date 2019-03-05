package com.pl.prod.utils.netty;

import android.util.Log;
import android.widget.Toast;

import com.pl.prod.app.PlApplication;

import io.netty.buffer.ByteBuf;

import java.util.*;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class NettyClientHandler extends SimpleChannelInboundHandler<Object> {
    private static final String TAG = "NettyClientHandler";

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        Log.i(TAG, "channelRead0: 客户端接受的消息: " + msg);
        ByteBuf in = (ByteBuf) msg;

        int i = 0;
        byte[] result = new byte[20];
        byte[] jwt = new byte[4];

        byte[] closeMsg = new byte[]{
                //A区头部
                (byte) 0xFF, (byte) 0x00,
                (byte) 0xAA,
                (byte) 0x00
        };
        try {
            while (in.isReadable()) {
                byte b = in.readByte();
                result[i] = b;
                i++;
            }
            Log.i(TAG, "channelRead0: " + toHexString(result));
            if (Arrays.equals(jwt, closeMsg)) {
                Toast.makeText(PlApplication.app, "成功", Toast.LENGTH_SHORT).show();
            } else {
                //上传到服务器中
                Log.i(TAG, "channelRead0 else: " + toHexString(result));
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Log.i(TAG, "channelRead0: 正在连接... ");
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Log.i(TAG, "channelRead0: 连接关闭! ");
        super.channelInactive(ctx);
    }


    /**
     * 数组转成十六进制字符串
     *
     * @param b
     * @return
     */
    public static String toHexString(byte[] b) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < b.length; ++i) {
            buffer.append(toHexString1(b[i]));
        }
        return buffer.toString();
    }


    public static String toHexString1(byte b) {
        String s = Integer.toHexString(b & 0xFF);
        if (s.length() == 1) {
            return "0" + s;
        } else {
            return s;
        }
    }
}
