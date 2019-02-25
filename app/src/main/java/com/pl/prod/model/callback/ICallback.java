package com.pl.prod.model.callback;

/**
 * Created by brander on 2017/9/24.
 */

public interface ICallback {
    /**
     * 成功回调
     */
    void setSuccess(Object message);

    /**
     * 失败回调
     */
    void setFailure(Object message);
}
