package com.tealcode.boxingspeed.utility;

/**
 * Created by Boswell Yu on 2017/10/5.
 */

public class AES {

    // Encrypt the srcdata with given ekey, encrypted data put into destbuff from offset
    public static int encrypt(byte[] srcdata, String ekey, byte[] destbuff, int offset)
    {
        // TODO: Encrypt Real data
        System.arraycopy(srcdata, 0, destbuff, offset, srcdata.length);

        return 0;

    }
}
