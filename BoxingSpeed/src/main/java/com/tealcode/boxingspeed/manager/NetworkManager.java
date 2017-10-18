package com.tealcode.boxingspeed.manager;

import android.util.Log;

import com.google.protobuf.InvalidProtocolBufferException;
import com.tealcode.boxingspeed.handler.IConnectHandler;
import com.tealcode.boxingspeed.helper.GateKeeper;
import com.tealcode.boxingspeed.helper.packer.ProtobufPacker;
import com.tealcode.boxingspeed.helper.packer.XMessage;
import com.tealcode.boxingspeed.protobuf.Client;
import com.tealcode.boxingspeed.protobuf.Server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Boswell Yu on 2017/10/2.
 */

public class NetworkManager {

    private static final String TAG = "NetworkManager";
    private static final int READ_BUFFER_SIZE = 8192;
    private static final int RECEIVE_BUFFER_SIZE = 32768;
    private static final int SERVER_MESSAGE_HEADER_SIZE = 16;

    // 限制每帧中发出去的网络包数量
    private static final int PACKET_LIMIT_PER_FRAME = 3;

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
    private Thread sendThread = null;
    private Thread recvThread = null;
    private Socket clientSocket = null;
    private long lastCheckTime = 0;

    private boolean connecting = false;
    private boolean connected = false;

    private BufferedOutputStream buffOutputStream = null;
    private BufferedInputStream  buffInputStream = null;

    private byte[] readBuffer;
    private byte[] receiveBuffer;
    private int    receiveOffset;
    private int    receiveLength;

    private ConcurrentLinkedQueue<byte[]> sendQueue;
    private ConcurrentLinkedQueue<Server.ServerMsg> recvQueue;

    private IConnectHandler connectHander = null;

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

        sendQueue = new ConcurrentLinkedQueue<>();
        recvQueue = new ConcurrentLinkedQueue<>();

        this.inited = true;
    }

    // This function should be called from one separate thread besides UI Main, or from AsyncTask
    public int connect() {

        if (!inited) {
            Log.e(TAG, "Not initialized yet!");
            return -1;
        }

        if (connecting) {
            return 0;
        }

        // 设置连接状态，避免重入
        connecting = true;

        // 结束掉以前的线程

        if(sendThread != null) {
            sendThread.interrupt();
        }

        if(recvThread != null) {
            recvThread.interrupt();
        }

        try {
            clientSocket = new Socket(serverIp, serverPort);
            buffOutputStream = new BufferedOutputStream(clientSocket.getOutputStream());
            buffInputStream = new BufferedInputStream(clientSocket.getInputStream());

            // 标记连接成功
            connecting = false;
            connected = true;

            sendThread = new Thread() {
                @Override
                public void run() {
                    super.run();
                    while(true) {
                        processSend();

                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            Log.d(TAG, "Send Thread Interrupted");
                        }
                    }
                }
            };
            sendThread.start();

            recvThread = new Thread() {
                @Override
                public void run() {
                    super.run();
                    while(true) {
                        if(processRecv()) {
                            processMessage();
                        }

                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            Log.e(TAG, "Receive Thread Interrupted");
                        }
                    }
                }
            };
            recvThread.start();

            if(connectHander != null) {
                connectHander.connectedCallback();
            }

        } catch (IOException e) {
            Log.e(TAG, "Create Socket Failed With Error: " + e.toString());
            return -1;
        }

        return 0;
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
        sendQueue.add(msgbytes);
    }

    public void registerConnectHandler(IConnectHandler handler)
    {
        this.connectHander = handler;
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
        int sendCount = 0;
        // 限制每帧中发出去的消息数量
        while(!sendQueue.isEmpty() && sendCount < PACKET_LIMIT_PER_FRAME) {
            byte[] msgdata = sendQueue.poll();

            try {

                buffOutputStream.write(msgdata);
                buffOutputStream.flush();

                sendCount++;
            } catch (IOException e) {
                Log.e(TAG, "IOException when write to Output Stream");
            }
        }

        long currTime = System.currentTimeMillis();
        if(currTime - lastCheckTime > 2000) {
            checkServerStatus();
            lastCheckTime = currTime;
        }
    }

    // 分发收到消息
    private void processMessage()
    {
        // 每帧仅处理有限个服务器消息
        int handledCount = 0;
        while(!recvQueue.isEmpty() && handledCount < PACKET_LIMIT_PER_FRAME)
        {
            // TODO: 分发给各个消息的订阅者去处理
            Server.ServerMsg message = recvQueue.poll();

            MessagePublisher.getInstance().publish(message);

            handledCount++;
        }
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
                    handleProtobufMessage(packetLen - 12, encrypt != 0);
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
            // TODO: 需要验证java中这种自身的拷贝会不会导致数据被覆盖
            System.arraycopy(receiveBuffer, receiveOffset, receiveBuffer, 0, receiveLength - receiveOffset);
            receiveOffset = 0;
            receiveLength -= receiveOffset;
        } else {
            // 错误情况，数据访问超出了边界，应该是程序内部的逻辑问题
            Log.e(TAG, "Internal Error: receive offset exceed receive length");
        }
    }

    private void checkServerStatus()
    {
        // TODO: Packet a ping message and send to server

        // Then check the time of last received pong from server, if longer than 6 seconds, consider
        // server has disconnected
    }

    private void handlePingPong()
    {
        // TODO: Hanlde PingPong Message
    }

    private void handleProtobufMessage(int messageLen, boolean encrypted)
    {
        int errorCode = XMessage.UNPACK_INT(receiveBuffer, receiveOffset);
        receiveOffset += 4;

        if(errorCode == 0) {

            Server.ServerMsg serverMsg = ProtobufPacker.decodeMessage(receiveBuffer, receiveOffset, messageLen - 4, encrypted);

            if(serverMsg != null) {
                recvQueue.add(serverMsg);
                Log.d(TAG, "Parse Message success, added to message queue");
            }
            else {
                Log.e(TAG, "Parse Server Message Failed!");
            }
        }

        receiveOffset += messageLen - 4;
    }

}
