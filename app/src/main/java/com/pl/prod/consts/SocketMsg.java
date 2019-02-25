package com.pl.prod.consts;

import com.pl.prod.utils.HexUtil;

public class SocketMsg {
    //tcp 信息
    public static byte[] tcpMsg = new byte[]{
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
            (byte) 0x39, (byte) 0x30, (byte) 0x39, (byte) 0x30
    };

    //wifi发送 Tenda_2644B0
    public static byte[] wifiMsg = new byte[]{
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
    public static byte[] sdMsg = new byte[]{
            //A区头部
            (byte) 0xFF, (byte) 0x00,
            (byte) 0xFA,
            (byte) 0x06,
            //B区消息
            (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x08,
            (byte) 0x00, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x0A, (byte) 0x65, (byte) 0x56, (byte) 0x6E
    };


    //绑定需求
    public static byte[] bindMsg = new byte[]{
            //A区头部
            (byte) 0xFF, (byte) 0x00,
            (byte) 0xAC,
            (byte) 0x06,
    };


    //断开连接
    public static byte[] closeMsg = new byte[]{
            //A区头部
            (byte) 0xFF, (byte) 0x00,
            (byte) 0xAD,
            (byte) 0x07
    };


    public static String stringToAscii(String value) {
        StringBuffer sbu = new StringBuffer();
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (i != chars.length - 1) {
                sbu.append((int) chars[i]).append(",");
            } else {
                sbu.append((int) chars[i]);
            }
        }
        Integer or = Integer.parseInt(sbu.toString());
        return Integer.toHexString(or);
    }

    public static void main(String[] args) {
        System.out.println(stringToAscii("2"));
    }
}
