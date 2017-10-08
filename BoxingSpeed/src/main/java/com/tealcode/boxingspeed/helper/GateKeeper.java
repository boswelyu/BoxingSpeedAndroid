package com.tealcode.boxingspeed.helper;

/**
 * Created by YuBo on 2017/9/29.
 */

public class GateKeeper {
    private static String SessionKey = "";
    private static int    platform = 1;     // 1: Android; 2: IOS
    private static String version = "";

    public static String getSessionKey() {
        return SessionKey;
    }

    public static void setSessionKey(String sessionKey) {
        SessionKey = sessionKey;
    }

    public static int getPlatform() {
        return platform;
    }

    public static void setPlatform(int platform) {
        GateKeeper.platform = platform;
    }

    public static String getVersion() {
        return version;
    }

    public static void setVersion(String version) {
        GateKeeper.version = version;
    }
}
