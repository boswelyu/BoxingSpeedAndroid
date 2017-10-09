package com.tealcode.boxingspeed.manager;

import android.util.Log;

import com.google.protobuf.InvalidProtocolBufferException;
import com.tealcode.boxingspeed.helper.GateKeeper;
import com.tealcode.boxingspeed.helper.packer.ProtobufPacker;
import com.tealcode.boxingspeed.helper.packer.XMessage;
import com.tealcode.boxingspeed.protobuf.Client;
import com.tealcode.boxingspeed.protobuf.Server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;

/**
 * Created by Boswell Yu on 2017/10/2.
 */

public class NetworkManager {

    private static final String TAG = "NetworkManager";
    private static final int READ_BUFFER_SIZE = 8192;
    private static final int RECEIVE_BUFFER_SIZE = 32768;
    private static final int SERVER_MESSAGE_HEADER_SIZE = 16;

    private static NetworkManager instance = null;
    public static NetworkManager getInstance() {
        if(instance == null) {
            instance = new NetworkManager();
        }
        return instance;
    }

    // Private Fields
    private boolean inited = false;
    private String serverIp;
    private int    serverPort;
    private Thread workingThread = null;

    private boolean connecting = false;
    private boolean connected = false;

    private BufferedOutputStream buffOutputStream = null;
    private BufferedInputStream  buffInputStream = null;

    private byte[] readBuffer;
    private byte[] receiveBuffer;
    private int    receiveOffset;
    private int    receiveLength;

    private LinkedList<byte[]> sendQueue;
    private LinkedList<Server.ServerMsg> recvQueue;

    public void init(String ip, int port)
    {
        // TODO: Check IPv6 address and convert
        this.serverIp = ip;
        this.serverPort = port;

        connected = false;
        connecting = false;

        readBuffer = new byte[READ_BUFFER_SIZE];
        receiveBuffer = new byte[RECEIVE_BUFFER_SIZE];
        receiveOffset = 0;
        receiveLength = 0;

        this.inited = true;
    }

    public void connect() {

        if (!inited) {
            Log.e(TAG, "Not initialized yet!");
            return;
        }

        if (connecting) {
            return;
        }

        // 设置连接状态，避免重入
        connecting = true;

        // 结束掉以前的线程
        if (workingThread != null) {
            workingThread.interrupt();
            connected = false;
        }

        workingThread = new Thread() {

            private boolean finished = false;

            @Override
            public synchronized void start() {
                super.start();
                finished = false;
            }

            @Override
            public void run() {
                super.run();

                try {
                    Socket clientSocket = new Socket(serverIp, serverPort);

                    buffOutputStream = new BufferedOutputStream(clientSocket.getOutputStream());
                    buffInputStream = new BufferedInputStream(clientSocket.getInputStream());



                    // 标记连接成功
                    connecting = false;
                    connected = true;

                    // 以固定帧率检查数据接收和发送
                    while (!finished) {
                        if (processRecv()) {
                            processSend();
                        }
                        // 分发收到的服务器消息
                        processMessage();
                    }

                } catch (IOException e) {
                    Log.e(TAG, "Create Socket Failed With Error: " + e.toString());
                    // TODO: Call Connection Failure Callback
                }
            }

            @Override
            public void interrupt() {
                super.interrupt();
                finished = true;
            }
        };

        workingThread.start();
    }

    // 对外Send接口
    public void sendProtobufMessage(Client.ClientMsg.Builder builder)
    {
        int userId = ProfilerManager.getUserId();

        Client.ProtoHead.Builder headBuilder = Client.ProtoHead.newBuilder();
        headBuilder.setPlatform(GateKeeper.getPlatform());
        headBuilder.setTimestamp((int)System.currentTimeMillis());
        headBuilder.setUserId(userId);
        headBuilder.setVersion(GateKeeper.getVersion());

        builder.setProtoHead(headBuilder.build());
        Client.ClientMsg clientMsg = builder.build();

        // Encode the message, queue to send buffer
        byte[] msgbytes = ProtobufPacker.encodeMessage(clientMsg.toByteArray(), userId, true);
        sendQueue.addLast(msgbytes);
    }

    // 处理接收到的buffer数据
    private boolean processRecv()
    {
        // 把输入流上接收到的数据依次放入缓冲区
        try {
            int recvSize = buffInputStream.read(readBuffer);

            if(recvSize > 0) {

                if (recvSize + receiveLength > RECEIVE_BUFFER_SIZE) {
                    Log.e(TAG, "Receive Buffer Full (This should not happen, check your Network Data process logic)");
                    return false;
                }

                System.arraycopy(readBuffer, 0, receiveBuffer, receiveLength, recvSize);
                receiveLength += recvSize;
            }

            parsePackage();

        } catch (IOException e) {
            Log.e(TAG, "IOException in processRecv, Reconnect to Server");
            // TODO: Reconnect to Server
        }
        return true;
    }

    // 处理消息发送队列
    private void processSend()
    {

    }

    // 分发收到消息
    private void processMessage()
    {

    }

    private void parsePackage()
    {
        // 读取receiveBuffer中的数据，解析成单独的消息
        if(receiveLength < SERVER_MESSAGE_HEADER_SIZE) {
            return;
        }

        while(receiveOffset < receiveLength) {
            byte headVer = XMessage.UNPACK_BYTE(receiveBuffer, receiveOffset);
            receiveOffset += 1;

            byte encrypt = XMessage.UNPACK_BYTE(receiveBuffer, receiveOffset);
            receiveOffset += 1;

            short headLen = XMessage.UNPACK_SHORT(receiveBuffer, receiveOffset);
            receiveOffset += 2;

            int packetLen = XMessage.UNPACK_INT(receiveBuffer, receiveOffset);
            receiveOffset += 4;

            if (packetLen > receiveLength - receiveOffset + 8) {
                // 包数据还没有接收完全
                receiveOffset -= 8;
                break;
            }

            int command = XMessage.UNPACK_INT(receiveBuffer, receiveOffset);
            receiveOffset += 4;

            switch(command) {
                case XMessage.PINGPONG_MESSAGE:
                    handlePingPong();
                    break;
                case XMessage.PROTOBUF_MESSAGE:
                    handleProtobufMessage(packetLen - 12);
                    break;
            }
        }

        // 把处理完成的数据清理出接收缓存
        if(receiveOffset == receiveLength) {
            // 数据结尾刚好是一个完整的消息结束，简单的重置索引值就可以了
            receiveOffset = 0;
            receiveLength = 0;
        } else if(receiveLength > receiveOffset){
            // 数据结尾没有对齐到完整的消息结束，需要保留剩余的数据，和后面收到的数据进行合并
            // TODO: 需要验证java中这个自身的拷贝会不会导致数据被覆盖
            System.arraycopy(receiveBuffer, receiveOffset, receiveBuffer, 0, receiveLength - receiveOffset);
            receiveOffset = 0;
            receiveLength -= receiveOffset;
        } else {
            // 错误情况，数据访问超出了边界，应该是程序内部的逻辑问题
            Log.e(TAG, "Internal Error: receive offset exceed receive length");
        }
    }

    private void handlePingPong()
    {
        // TODO: Hanlde PingPong Message
    }

    private void handleProtobufMessage(int messageLen)
    {
        int errorCode = XMessage.UNPACK_INT(receiveBuffer, receiveOffset);
        receiveOffset += 4;

        if(errorCode == 0) {
            byte[] msgdata = XMessage.UNAPCK_BYTES(receiveBuffer, receiveOffset, messageLen - 4);

            try {
                Server.ServerMsg serverMsg = Server.ServerMsg.parseFrom(msgdata);

                if(serverMsg != null) {
                    recvQueue.addLast(serverMsg);
                }

            } catch (InvalidProtocolBufferException e) {
                Log.e(TAG, "Protobuf Message Parse Failed with exception");
            }
        }

        receiveOffset += messageLen - 4;
    }

}
