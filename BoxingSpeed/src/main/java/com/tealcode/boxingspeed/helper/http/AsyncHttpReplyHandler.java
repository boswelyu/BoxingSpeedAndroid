package com.tealcode.boxingspeed.helper.http;

/**
 * Created by YuBo on 2017/10/24.
 */

public abstract class AsyncHttpReplyHandler {

    public abstract void onSuccess(String replyStr);

    public abstract void onFailure(int statusCode, String errorInfo);
}
