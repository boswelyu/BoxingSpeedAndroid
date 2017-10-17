package com.tealcode.boxingspeed.handler;

/**
 * Created by YuBo on 2017/9/18.
 */

public interface IRegisterReplyHandler {
    void onRegisterSuccess();
    void onRegisterFailure(int retCode, String errorInfo);
}
