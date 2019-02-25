package com.pl.prod.entity;

/**
 * Created by brander on 2019/1/12
 */
public class ResultJson<T> {
    private Integer code;

    private String message;

    private T data;

    public ResultJson() {
    }

    private ResultJson(int code) {
        this.code = code;
    }

    private ResultJson(int code, T data) {
        this.code = code;
        this.data = data;
    }

    private ResultJson(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    private ResultJson(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


    @Override
    public String toString() {
        return "ResultJson{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
