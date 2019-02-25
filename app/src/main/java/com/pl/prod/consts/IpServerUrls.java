package com.pl.prod.consts;

import com.pl.prod.app.PlApplication;

public class IpServerUrls {

    /**
     * ip 服务器 token
     */
    public static final String GET_IP_TOKEN = PlApplication.BASE_IP_URL + "v1/app/token/";
    /**
     * ip 服务器 获取 ip
     */
    public static final String GET_IP = PlApplication.BASE_IP_URL + "v1/app/ip/";
    /**
     * ip 服务器获取版本号
     */
    public static final String GET_VERSION = PlApplication.BASE_IP_URL + "/v1/app/version/";

}
