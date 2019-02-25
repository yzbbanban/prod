package com.pl.prod.utils;

import android.util.Log;

import com.pl.prod.app.PlApplication;


/**
 * Created by brander on 2016/8/17.
 * 打印工具
 */
public class LogUtil {
    public synchronized static void info(String key, String message) {
        if (!PlApplication.isOnAppStore) {
            Log.i(key, message);
        }

    }

    public synchronized static void debug(String key, String message) {
        if (!PlApplication.isOnAppStore) {
            Log.d(key, message);
        }

    }

    public synchronized static void err(String key, String message) {
        if (!PlApplication.isOnAppStore) {
            Log.e(key, message);
        }

    }

}
