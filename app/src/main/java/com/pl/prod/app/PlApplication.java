package com.pl.prod.app;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.os.Vibrator;
import android.support.multidex.MultiDex;

import com.pl.prod.consts.IpServerUrls;
import com.pl.prod.entity.User;
import com.pl.prod.utils.LogUtil;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import org.litepal.LitePalApplication;

import java.util.LinkedList;
import java.util.List;


/**
 * Created by brander on 2017/9/21.
 * 全局变量存储
 */
public class PlApplication extends LitePalApplication {
    private static final String TAG = "PlApplication";
    private static Context context;
    public static PlApplication app;
    public static boolean isOnAppStore = false;//是否上架
    public static boolean isUserEdit = true;//是否已设置过用户信息
    public static String url = "";
    //测试版本
    public static String BASE_IP_URL = "http://39.108.103.128/";
    public static String BASE_SERVER_URL = "http://39.108.103.128:9095/";
    public static IpServerUrls urls;
    public static User user = new User();//用户数据全局单例保存

    public static String WIFI="";
    public static String WIFI_SECRET="";

    //使用list來保存每一個activity
    private List<Activity> mList = new LinkedList<Activity>();
    //使用list來保存每一個service
    private List<Service> sList = new LinkedList<Service>();
    public Vibrator mVibrator;
    public static String ipToken;
    public static String userToken;

    @Override
    public void onCreate() {
        super.onCreate();
        urls = new IpServerUrls();
        context = getApplicationContext();

        app = this;
        mVibrator = (Vibrator) getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
        ZXingLibrary.initDisplayOpinion(this);
        MultiDex.install(this);
    }


    /**
     * 获取全局上下文对象
     *
     * @return
     */
    public static Context getAppContext() {
        return context;
    }


    // add Activity
    public void addActivity(Activity activity) {
        mList.add(activity);
    }

    // add Service
    public void addService(Service service) {
        sList.add(service);
    }

    //關閉每一個list內的activity與Service
    public void exit() {

        try {
            for (Service service : sList) {
                if (sList != null)
                    service.stopSelf();
            }
            for (Activity activity : mList) {
                if (activity != null)
                    LogUtil.info(TAG, "exit: " + activity.getClass());
                activity.finish();
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }

    //殺掉process
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }
}
