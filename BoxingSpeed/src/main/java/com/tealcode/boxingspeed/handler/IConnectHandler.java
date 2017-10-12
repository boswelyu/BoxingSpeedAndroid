package com.tealcode.boxingspeed.handler;

/**
 * Created by YuBo on 2017/10/12.
 */

public interface IConnectHandler {
    void connectedCallback();
    void disconnectedCallback();
    void timeoutCallback();
}
