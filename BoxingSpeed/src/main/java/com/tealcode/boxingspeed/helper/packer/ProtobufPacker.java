package com.tealcode.boxingspeed.helper.packer;

import android.util.Log;

import com.google.protobuf.InvalidProtocolBufferException;
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
        byte[] encrydata = data;
        if(encrypt) {
            encrydata = AES.encrypt(data);
            if(encrydata == null) {
                // encrypt failed
                Log.e(TAG, "Encrypt Message Failed!");
                return null;
            }
        }

        int packlen = encrydata.length + 16;
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

        System.arraycopy(encrydata, 0, packdata, offset, encrydata.length);

        return packdata;
    }


    /*  Buffer format:
    *      4          ...
    *  ErrorCode | Proto Data
    */
    public static Server.ServerMsg decodeMessage(byte[] buffer, int offset, int length, boolean encrypted)
    {
        Server.ServerMsg serverMsg = null;
        byte[] data = new byte[length];
        System.arraycopy(buffer, offset, data, 0, length);

        byte[] decoded_data;
        if(encrypted) {
            decoded_data = AES.decrypt(data);
        }
        else {
            decoded_data = data;
        }

        try {
            serverMsg = Server.ServerMsg.parseFrom(XMessage.UNAPCK_BYTES(decoded_data, 0, length));
        } catch (InvalidProtocolBufferException e) {
            Log.e(TAG, "Decode Server Message Failed with exception: " + e.toString());
        }


        return serverMsg;
    }

}
