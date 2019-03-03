package com.pl.prod.utils.netty;

import android.util.Log;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class NettyClientHandler extends SimpleChannelInboundHandler<Object> {
    private static final String TAG = "NettyClientHandler";
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        Log.i(TAG, "channelRead0: 客户端接受的消息: " + msg);
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
}
