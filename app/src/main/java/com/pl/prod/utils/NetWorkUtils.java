package com.pl.prod.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by brander on 2017/11/28.
 * 网络状态
 */

public class NetWorkUtils {
    /**
     * 获取当前网络类型
     *
     * @param context
     * @return -1 没有网络
     * 0 移动网络;
     * 1 wifi;
     * 2 其他；
     * @throws Exception
     */
    public static int getNetWorkType(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork == null)
            return -1;
        if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
            return 1;
        } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
            return 0;
        } else {
            return 2;
        }
    }

    public static boolean isMobileType(Context context) {
        return 0 == getNetWorkType(context);
    }

    public static boolean isConnected(Context context) {
        return -1 != getNetWorkType(context);
    }

    public static void openWifi(Activity context) {
        context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
    }

    /**
     * Convert a IPv4 address from an integer to an InetAddress.
     *
     * @param hostAddress an int corresponding to the IPv4 address in network byte order
     */
    public static InetAddress intToInetAddress(int hostAddress) {
        byte[] addressBytes = {(byte) (0xff & hostAddress), (byte) (0xff & (hostAddress >> 8)),
                (byte) (0xff & (hostAddress >> 16)), (byte) (0xff & (hostAddress >> 24))};

        try {
            return InetAddress.getByAddress(addressBytes);
        } catch (UnknownHostException e) {
            throw new AssertionError();
        }
    }

    /**
     * Convert a IPv4 address from an InetAddress to an integer
     *
     * @param inetAddr is an InetAddress corresponding to the IPv4 address
     * @return the IP address as an integer in network byte order
     */
    public static int inetAddressToInt(InetAddress inetAddr) throws IllegalArgumentException {

        byte[] addr = inetAddr.getAddress();
        return ((addr[3] & 0xff) << 24) | ((addr[2] & 0xff) << 16) | ((addr[1] & 0xff) << 8) | (addr[0] & 0xff);
    }
}
