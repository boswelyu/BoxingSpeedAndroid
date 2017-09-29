package com.tealcode.boxingspeed.helper;

/**
 * Created by YuBo on 2017/9/29.
 */

public class GateKeeper {
    private static String SessionKey = "";

    public static String getSessionKey() {
        return SessionKey;
    }

    public static void setSessionKey(String sessionKey) {
        SessionKey = sessionKey;
    }
}
