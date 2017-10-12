package com.tealcode.boxingspeed.utility;

import android.util.Log;

import java.security.MessageDigest;

/**
 * Created by YuBo on 2017/10/12.
 */

public class MD5Util {

    private static final String TAG = "MD5Util";
    private static MessageDigest md5Instance = null;

    public static String encode(String message) {
        String md5str = "";
        try {

            if(md5Instance == null) {
                md5Instance = MessageDigest.getInstance("MD5");
            }

            byte[] input = message.getBytes();
            byte[] buff = md5Instance.digest(input);

            md5str = bytesToHex(buff);

        } catch (Exception e) {
            Log.e(TAG, "Encode MD5 encountered exception: " + e.toString());
        }
        return md5str;
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuffer md5str = new StringBuffer();
        // 把数组每一字节换成16进制连成md5字符串
        int digital;
        for (int i = 0; i < bytes.length; i++) {
            digital = bytes[i];

            if (digital < 0) {
                digital += 256;
            }
            if (digital < 16) {
                md5str.append("0");
            }
            md5str.append(Integer.toHexString(digital));
        }
        return md5str.toString().toLowerCase();
    }
}
