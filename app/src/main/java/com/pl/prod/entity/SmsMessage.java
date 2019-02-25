package com.pl.prod.entity;


/**
 * 发送信息
 */
public class SmsMessage {

    /**
     * 国家代码
     */
    private String countryCode;

    /**
     * 手机号码
     */
    private String phoneNumber;

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "SmsMessageDTO{" +
                "countryCode='" + countryCode + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
