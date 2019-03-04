package com.pl.prod.utils.netty;

import android.util.Log;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
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


        try {
            while (in.isReadable()) {
                byte b = in.readByte();
                result[i] = b;
                i++;
            }
            System.out.println(toHexString(result));

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
