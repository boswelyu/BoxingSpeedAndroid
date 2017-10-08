package com.tealcode.boxingspeed.manager;

import android.util.Log;

import com.tealcode.boxingspeed.helper.GateKeeper;
import com.tealcode.boxingspeed.helper.packer.ProtobufPacker;
import com.tealcode.boxingspeed.protobuf.Client;
import com.tealcode.boxingspeed.protobuf.Server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;

/**
 * Created by Boswell Yu on 2017/10/2.
 */

public class NetworkManager {

    private static final String TAG = "NetworkManager";

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

    private LinkedList<byte[]> sendQueue;
    private LinkedList<Server.ServerMsg> recvQueue;

    public void init(String ip, int port)
    {
        // TODO: Check IPv6 address and convert
        this.serverIp = ip;
        this.serverPort = port;

        connected = false;
        connecting = false;

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
}
