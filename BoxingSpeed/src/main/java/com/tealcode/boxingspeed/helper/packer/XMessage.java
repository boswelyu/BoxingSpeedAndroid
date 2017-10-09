package com.tealcode.boxingspeed.helper.packer;

/**
 * Created by Boswell Yu on 2017/10/2.
 * 所有发往服务器的消息的包装消息
 * 固定的消息头：
 *    1        1          2          4            4        4
 * Version | Encrypt | Head Len | Packet Len | Command | UserId | Real Data
 */


public class XMessage {

    public static final int PINGPONG_MESSAGE = 0x00000001;
    public static final int PROTOBUF_MESSAGE = 0x00000002;




    private static final String TAG = "XMessage";
    // Data Type Length
    public static int XSIZE_OF(byte x) {return 1;}
    public static int XSIZE_OF(char x) {return 1;}
    public static int XSIZE_OF(short x) {return 2;}
    public static int XSIZE_OF(int x) {return 4;}
    public static int XSIZE_OF(long x) {return 8;}

    public static int XSIZE_OF(byte[] data)
    {
        if(data.length > 0x2ffffff) return 0;
        return data.length;
    }

    // API which pack data into given buffer
    public static int PACK(byte x, byte[] buffer, int offset) {
        buffer[offset] = x;
        return offset + XSIZE_OF(x);
    }

    public static int PACK(char x, byte[] buffer, int offset) {
        buffer[offset] = (byte)x;
        return offset + XSIZE_OF(x);
    }

    public static int PACK(short x, byte[] buffer, int offset) {
        buffer[offset] = (byte)x;
        buffer[offset + 1] = (byte)(x >> 8);
        return offset + XSIZE_OF(x);
    }

    public static int PACK(int x, byte[] buffer, int offset) {
        buffer[offset] = (byte)x;
        buffer[offset + 1] = (byte)(x >> 8);
        buffer[offset + 2] = (byte)(x >> 16);
        buffer[offset + 3] = (byte)(x >> 24);
        return offset + XSIZE_OF(x);
    }

    public static int PACK(long x, byte[] buffer, int offset) {
        buffer[offset] = (byte)x;
        buffer[offset + 1] = (byte)(x >> 8);
        buffer[offset + 2] = (byte)(x >> 16);
        buffer[offset + 3] = (byte)(x >> 24);
        buffer[offset + 4] = (byte)(x >> 32);
        buffer[offset + 5] = (byte)(x >> 40);
        buffer[offset + 6] = (byte)(x >> 48);
        buffer[offset + 7] = (byte)(x >> 56);

        return offset + XSIZE_OF(x);
    }

    public static int PACK(byte[] x, byte[] buffer, int offset)
    {
        System.arraycopy(x, 0, buffer, offset, x.length);
        return offset + x.length;
    }

    public static byte UNPACK_BYTE(byte[] buffer, int offset)
    {
        return buffer[offset];
    }

    public static short UNPACK_SHORT(byte[] buffer, int offset)
    {
        return (short)((buffer[offset]&0xff) |
                ((buffer[offset + 1] & 0xff) << 8));
    }

    public static int UNPACK_INT(byte[] buffer, int offset)
    {
        return (int)((buffer[offset] & 0xff) |
                ((buffer[offset + 1] & 0xff) << 8) |
                ((buffer[offset + 2] & 0xff) << 16) |
                ((buffer[offset + 3] & 0xff) << 24) );
    }

    public static long UNPACK_LONG(byte[] buffer, int offset)
    {
        return  ((long)(buffer[offset] & 0xff) |
                ((long)(buffer[offset + 1] & 0xff) << 8) |
                ((long)(buffer[offset + 2] & 0xff) << 16) |
                ((long)(buffer[offset + 3] & 0xff) << 24) |
                ((long)(buffer[offset + 4] & 0xff) << 32) |
                ((long)(buffer[offset + 5] & 0xff) << 40) |
                ((long)(buffer[offset + 6] & 0xff) << 48) |
                ((long)(buffer[offset + 7] & 0xff) << 56));
    }

    public static byte[] UNAPCK_BYTES(byte[] buffer, int offset, int length)
    {
        byte[] bytes = new byte[length];
        System.arraycopy(buffer, offset, bytes, 0, length);
        return bytes;
    }
}
