package com.tealcode.boxingspeed.message;

/**
 * Created by YuBo on 2017/10/24.
 */

public class BasicHttpReply {
    private int  status;
    private String errorInfo;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }
}
