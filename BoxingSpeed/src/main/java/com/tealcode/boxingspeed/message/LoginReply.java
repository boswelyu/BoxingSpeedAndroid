package com.tealcode.boxingspeed.message;

/**
 * Created by YuBo on 2017/9/22.
 */

public class LoginReply extends BasicHttpReply{

    private String  userId;
    private String  serverIp;
    private int     serverPort;
    private String  sessionKey;

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
