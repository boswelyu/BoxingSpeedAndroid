package com.tealcode.boxingspeed.handler;

/**
 * Created by YuBo on 2017/10/26.
 */

public abstract class PhoneVerifyHandler {

    abstract public void onSuccess(String phoneNum);

    abstract public void onFailure(String reason);
}
