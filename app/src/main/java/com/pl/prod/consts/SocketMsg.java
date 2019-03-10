package com.pl.prod.consts;


public class SocketMsg {
    //tcp 信息
    public byte[] tcpMsg;

    //wifi发送 Tenda_2644B0
    public byte[] wifiMsg;


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
