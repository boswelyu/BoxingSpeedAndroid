package com.tealcode.boxingspeed.message;

/**
 * Created by YuBo on 2017/9/22.
 */

public class LoginReply {
    private int  status;
    private String errorInfo;
    private String  userId;
    private String  serverIp;
    private int     serverPort;
    private String  sessionKey;

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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String key) {
        this.sessionKey = key;
    }
}
