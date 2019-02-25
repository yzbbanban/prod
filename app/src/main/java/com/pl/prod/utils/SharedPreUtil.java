package com.pl.prod.utils;

import android.content.Context;
import android.content.SharedPreferences;


import com.pl.prod.entity.User;

import static android.content.Context.MODE_PRIVATE;

public class SharedPreUtil {

    private static final String TAG = "SharedPreUtil";

    /**
     * 保存用户
     *
     * @param context  context
     * @param username 用户
     * @param password 密码
     */
    public static void saveUserSp(Context context, String username, String password) {
        SharedPreferences.Editor editor = context.getSharedPreferences("user", MODE_PRIVATE).edit();
        editor.putString("name", username);
        editor.putString("password", password);
        editor.commit();
    }

    /**
     * 获取用户信息
     *
     * @param context  context
     * @return 用户信息
     */
    public static User getUserSp(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("user", MODE_PRIVATE);
        String name = preferences.getString("name", "");
        String password = preferences.getString("password", "");
        User user = new User(name, password);
        return user;
    }

    /**
     * 保存api 信息
     *
     * @param context  context
     * @param ip       ip
     * @param username 用户
     */
    public static void saveApiSp(Context context, String ip, String username) {
        SharedPreferences.Editor editor = context.getSharedPreferences("api" + username, MODE_PRIVATE).edit();
        editor.putString("ip", ip);
        editor.commit();
    }

    /**
     * 获取 ip 信息
     *
     * @param context  context
     * @param username 用户
     * @return ip 信息
     */
    public static String getAPiSp(Context context, String username) {
        SharedPreferences preferences = context.getSharedPreferences("api" + username, MODE_PRIVATE);
        String ip = preferences.getString("ip", "");
        return ip;
    }

}
