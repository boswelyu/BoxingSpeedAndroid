package com.tealcode.boxingspeed.handler;

import com.tealcode.boxingspeed.message.LoginReply;
import com.tealcode.boxingspeed.message.RegisterReply;
import com.tealcode.boxingspeed.message.SocketEvent;

/**
 * Created by YuBo on 2017/9/18.
 */

public interface IRegisterReplyHandler {
    public void onRegisterReply(RegisterReply reply);
    public void onSocketEvent(SocketEvent event);
}
