package com.tealcode.boxingspeed.handler;

import com.tealcode.boxingspeed.message.LoginReply;
import com.tealcode.boxingspeed.message.SocketEvent;

/**
 * Created by YuBo on 2017/9/18.
 */

public interface ILoginReplyHandler {
    public void onLoginSuccess();
    public void onLoginFailure(String reason);
    public void onSocketEvent(SocketEvent event);
}
