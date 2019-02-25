package com.pl.prod.entity;

/**
 * 用户注册
 */
public class UserRegister {
    /**
     * 密码
     */
    private String password;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 国家代码
     */
    private String countryCode;
    /**
     * 手机验证码
     */
    private String code;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "UserRegister{" +
                "password='" + password + '\'' +
                ", mobile='" + mobile + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
