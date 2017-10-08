package com.tealcode.boxingspeed.helper.packer;

import android.util.Log;

import com.tealcode.boxingspeed.helper.GateKeeper;
import com.tealcode.boxingspeed.protobuf.Client;
import com.tealcode.boxingspeed.protobuf.Server;
import com.tealcode.boxingspeed.utility.AES;

/**
 * Created by Boswell Yu on 2017/10/2.
 */

public class ProtobufPacker {

    private static final String TAG = "ProtobufPacker";

    /*  固定的消息头：
    *    1        1          2         4         4        4        ...
    * Version | Encrypt | Head Len | Length | Command | UserId | Proto Data
    *
    * Length: Total Length of Packed Message
    * Version: Version Number of message head
    * Head Len: The bytes of this Message Head
    * Command: Indicator of packed message type
    * UserId:  UserId who send this message
    * Encrypt: Encrypted or not
    * Proto Data: the encrypted protobuf message
    */
    public static byte[] encodeMessage(byte[] data, int userId, boolean encrypt)
    {
        int packlen = data.length + 16;
        byte[] packdata = new byte[packlen];
        int offset = 0;
        offset = XMessage.PACK((byte)1, packdata, offset);
        if(encrypt) {
            offset = XMessage.PACK((byte)1, packdata, offset);
        }else {
            offset = XMessage.PACK((byte)0, packdata, offset);
        }

        offset = XMessage.PACK((short)16, packdata, offset);

        offset = XMessage.PACK(packlen, packdata, offset);
        offset = XMessage.PACK(XMessage.PROTOBUF_MESSAGE, packdata, offset);
        offset = XMessage.PACK(userId, packdata, offset);

        int result = 0;
        if(encrypt) {
            result = AES.encrypt(data, GateKeeper.getSessionKey(), packdata, offset);
        }
        else {
            System.arraycopy(data, 0, packdata, offset, data.length);
        }

        if(result != 0) {
            // 加密成功，返回组装之后的数据
            Log.e(TAG, "Encrypt protobuf message failed with error: " + result);
        }
        return packdata;
    }

    public static Server.ServerMsg decodeMessage(byte[] buffer, int offset)
    {

        return null;
    }
}
