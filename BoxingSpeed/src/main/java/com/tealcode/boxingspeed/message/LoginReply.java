package com.tealcode.boxingspeed.message;

/**
 * Created by YuBo on 2017/9/22.
 */

public class LoginReply {
    private String  status;
    private String  serverIp;
    private int     serverPort;
    private String  sessionKey;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
