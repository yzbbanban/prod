package com.pl.prod.consts;

import com.pl.prod.app.PlApplication;

public class TcpServerUrls {

    /**
     * ftp 服务器 注册短信 POST
     */
    private static final String APP_FTP_SMS_REGISTER = "/v1/app/sms/";

    /**
     * ftp 服务器 找回密码短信  POST
     */
    private static final String APP_FTP_SMS_FIND_PWD = "/v1/app/sms/";

    /**
     * ftp 服务器 注册 POST
     */
    private static final String APP_FTP_REGISTER = "/v1/user/info/";

    /**
     * ftp 服务器 登录 POST
     */
    private static final String APP_FTP_LOGIN = "/v1/user/info/";

    /**
     * ftp 服务器 找回密码 POST
     */
    private static final String APP_FTP_FIND_PWD = "/v1/user/info/";

    /**
     * ftp 服务器 产品列表 GET
     */
    private static final String APP_FTP_PRODUCT_LIST = "/v1/app/product/user/";

    /**
     * ftp 服务器 产品更新 POST
     */
    private static final String APP_FTP_PRODUCT_UPDATE = "/v1/app/product/update";

    public static String getAppFtpSmsRegister() {
        return PlApplication.BASE_SERVER_URL + APP_FTP_SMS_REGISTER;
    }

    public static String getAppFtpSmsFindPwd() {
        return PlApplication.BASE_SERVER_URL + APP_FTP_SMS_FIND_PWD;
    }

    public static String getAppFtpRegister() {
        return PlApplication.BASE_SERVER_URL + APP_FTP_REGISTER;
    }

    public static String getAppFtpLogin() {
        return PlApplication.BASE_SERVER_URL + APP_FTP_LOGIN;
    }

    public static String getAppFtpFindPwd() {
        return PlApplication.BASE_SERVER_URL + APP_FTP_FIND_PWD;
    }

    public static String getAppFtpProductList() {
        return PlApplication.BASE_SERVER_URL + APP_FTP_PRODUCT_LIST;
    }

    public static String getAppFtpProductUpdate() {
        return PlApplication.BASE_SERVER_URL + APP_FTP_PRODUCT_UPDATE;
    }


//    /**
//     * ftp 服务器 设置用户信息 POST
//     */
//    private static final String APP_FTP_SET_USER_MSG = PlApplication.BASE_SERVER_URL + "/v1/user/info/update";

}
