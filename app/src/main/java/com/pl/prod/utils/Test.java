package com.pl.prod.utils;

import com.pl.prod.utils.netty.NettyClientHandler;

public class Test {
    public static void main(String[] args) {
        //00 14
        int c = 0xAA;
        for (int i = 3; i < tcpMsg2.length; i++) {
            Integer or = Integer.parseInt(HexUtil.byteToHex(tcpMsg2[i]), 16);
            c = c ^ or;
        }
        tcpMsg2[2] = (byte) c;


        System.out.println(NettyClientHandler.toHexString(tcpMsg2));
    }

    //tcp 信息
    public static byte[] tcpMsg2 = new byte[]{
            //A区头部
            (byte) 0xFF, (byte) 0x00,
            (byte) 0x00,
            (byte) 0x05,
            //B区消息
            (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x14,
            //地址  192.168.0.160    172.20.10.2
            (byte) 0x53, (byte) 0x4e, (byte) 0x43, (byte) 0x2d,
            (byte) 0x54, (byte) 0x65, (byte) 0x73, (byte) 0x74,
            (byte) 0x5f, (byte) 0x31, (byte) 0x00, (byte) 0x41, (byte) 0x62,
            //空字符
            (byte) 0x31,
            //端口 9654 9090
            (byte) 0x7e, (byte) 0x21, (byte) 0x40, (byte) 0x23, (byte) 0x24, (byte) 0x25
    };

}
