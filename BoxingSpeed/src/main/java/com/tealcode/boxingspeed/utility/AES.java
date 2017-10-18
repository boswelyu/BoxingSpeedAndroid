package com.tealcode.boxingspeed.utility;

import android.nfc.Tag;
import android.util.Log;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Boswell Yu on 2017/10/5.
 */

public class AES {

    private static String TAG = "AES";
    private static String CipherMode = "AES/ECB/PKCS5Padding";

    private static Cipher encrypter;
    private static Cipher decrypter;

    public static int init(String ekey)
    {

        try {
            SecretKeySpec skeySpec = new SecretKeySpec(ekey.getBytes("UTF-8"), "AES");
            encrypter = Cipher.getInstance(CipherMode);
            encrypter.init(Cipher.ENCRYPT_MODE, skeySpec);

            decrypter = Cipher.getInstance(CipherMode);
            decrypter.init(Cipher.DECRYPT_MODE, skeySpec);
        }catch (Exception e) {
            Log.e(TAG, "AES Init Failed with exception: " + e.toString());
            return -1;
        }
        return 0;
    }

    // Encrypt the srcdata with given ekey, encrypted data put into destbuff from offset
    public static byte[] encrypt(byte[] srcdata)
    {
        if(encrypter == null) {
            Log.e(TAG, "Encrypter Not Created Yet");
            return null;
        }

        byte[] result;
        try {
            result = encrypter.doFinal(srcdata);
        } catch (Exception e) {
            Log.e(TAG, "Encrypt Failed With Exception: " + e.toString());
            return null;
        }

        return result;
    }

    public static byte[] decrypt(byte[] content)
    {
        if(decrypter == null) {
            Log.e(TAG, "Decrypter Not Created Yet");
            return null;
        }

        byte[] result;
        try {
            result = decrypter.doFinal(content);
        }catch (Exception e) {
            Log.e(TAG, "Decrypt Failed With Exception: " + e.toString());
            return null;
        }

        return result;
    }


    // 字节数组转换成16进制字符串
    private static String byte2hex(byte[] inb)
    {
        StringBuffer sb = new StringBuffer(inb.length * 2);
        String temp;

        for(int i = 0; i < inb.length; i++)
        {
            temp = Integer.toHexString(inb[i] & 0xFF);
            if(temp.length() == 1) {
                sb.append("0");
            }
            sb.append(temp);
        }
        return sb.toString().toLowerCase();
    }

    // 16进制字符串转换成字节数组
    private static byte[] hex2byte(String instr)
    {
        if(instr == null || instr.length() < 2) {
            return new byte[0];
        }

        instr = instr.toLowerCase();
        int len = instr.length() / 2;
        byte[] result = new byte[len];
        String temp;

        for(int i = 0; i < len; i++)
        {
            temp = instr.substring(2 * i, 2 * i + 2);
            result[i] = (byte)(Integer.parseInt(temp, 16) & 0xFF);
        }
        return result;
    }
}
