package com.tealcode.boxingspeed.manager;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.tealcode.boxingspeed.config.AppConfig;
import com.tealcode.boxingspeed.handler.IConnectHandler;
import com.tealcode.boxingspeed.handler.IRegisterReplyHandler;
import com.tealcode.boxingspeed.helper.GateKeeper;
import com.tealcode.boxingspeed.message.LoginReply;
import com.tealcode.boxingspeed.message.LoginRequest;
import com.tealcode.boxingspeed.message.RegisterRequest;
import com.tealcode.boxingspeed.message.SocketEvent;
import com.tealcode.boxingspeed.handler.ILoginReplyHandler;
import com.tealcode.boxingspeed.protobuf.Client;
import com.tealcode.boxingspeed.protobuf.Common;
import com.tealcode.boxingspeed.protobuf.Server;
import com.tealcode.boxingspeed.utility.MD5Util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by YuBo on 2017/9/18.
 */

public class LoginManager extends Handler implements IConnectHandler {
    private static final String TAG = "LoginManager";
    private static LoginManager instance = null;
    public static LoginManager getInstance()
    {
        if(instance == null) {
            instance = new LoginManager();
        }
        return instance;
    }

    private ILoginReplyHandler mLoginReplyHander;
    private IRegisterReplyHandler mRegisterReplyHandler;
    private AsyncTask<Void, Void, String> loginTask = null;
    private AsyncTask<Void, Void, Integer> registerTask = null;
    private String errorInfo = "";

    private boolean loginFinished = false;
    private Common.RESULT  loginResult;

    private LoginManager()
    {
        MessagePublisher.getInstance().register(Server.LoginReply.class, this);
    }

    public void setRegisterHandler(IRegisterReplyHandler handler)
    {
        this.mRegisterReplyHandler = handler;
    }

    public void setLoginHandler(ILoginReplyHandler loginHandler) {
        this.mLoginReplyHander = loginHandler;
    }

    public void login(final String username, final String password, final boolean dohash)
    {
        if(loginTask != null) {
            loginTask.cancel(true);
        }

        loginTask = new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                URL url = null;
                String retstatus = "";
                try {
                    url = new URL(AppConfig.LoginUrl);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");

                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);

                    PrintWriter printWriter = new PrintWriter(urlConnection.getOutputStream());
                    printWriter.write(BuildLoginPostStr(username, password, dohash));
                    printWriter.flush();

                    // Read login response
                    BufferedInputStream bis = new BufferedInputStream(urlConnection.getInputStream());
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();

                    int len;
                    byte[] buffer = new byte[1024];
                    while((len = bis.read(buffer)) != -1)
                    {
                        bos.write(buffer, 0, len);
                        bos.flush();
                    }
                    bos.close();

                    String reply = bos.toString("utf-8");
                    retstatus = HandleLoginResult(reply);

                    if(retstatus.equals("OK")) {
                        // 继续等待处理完登录返回消息后继续
                        while(!loginFinished) {
                            Thread.sleep(100);
                        }

                        // 检查Login消息的返回码
                        return getLoginResult(loginResult);
                    }

                }catch(MalformedURLException urle) {
                    Log.e(TAG, "LoginURL Malformed");
                    HandleLoginSocketError(SocketEvent.ADDRESS_INVALID);
                }catch(IOException ioe) {
                    HandleLoginSocketError(SocketEvent.ADDRESS_CONNECT_FAILED);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                return retstatus;
            }

            @Override
            protected void onPostExecute(String status) {
                if(status.equals("OK")) {
                    // Login Success
                    if(mLoginReplyHander != null) {
                        mLoginReplyHander.onLoginSuccess();
                    }
                }else {
                    if(mLoginReplyHander != null) {
                        mLoginReplyHander.onLoginFailure(status);
                    }
                }
            }

        }.execute();
    }

    public void emailRegister(final String username, final String password)
    {
        if(registerTask != null) {
            registerTask.cancel(true);
        }

        registerTask = new AsyncTask<Void, Void, Integer>() {

            @Override
            protected Integer doInBackground(Void... params) {
                URL url = null;
                int retstatus = 0;
                try {
                    url = new URL(AppConfig.RegisterUrl);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");

                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);

                    PrintWriter printWriter = new PrintWriter(urlConnection.getOutputStream());
                    printWriter.write(BuildRegisterPostStr(username, password));
                    printWriter.flush();

                    // Read login response
                    BufferedInputStream bis = new BufferedInputStream(urlConnection.getInputStream());
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();

                    int len;
                    byte[] buffer = new byte[1024];
                    while((len = bis.read(buffer)) != -1)
                    {
                        bos.write(buffer, 0, len);
                        bos.flush();
                    }
                    bos.close();

                    String reply = bos.toString("utf-8");
                    retstatus = HandleRegisterResult(reply);


                }catch(MalformedURLException urle) {
                    Log.e(TAG, "LoginURL Malformed");
                    HandleLoginSocketError(SocketEvent.ADDRESS_INVALID);
                }catch(IOException ioe) {
                    HandleLoginSocketError(SocketEvent.ADDRESS_CONNECT_FAILED);
                }

                return retstatus;
            }

            @Override
            protected void onPostExecute(Integer status) {
                int retstatus = status.intValue();
                if(retstatus == 0) {
                    // Login Success
                    if(mRegisterReplyHandler != null) {
                        mRegisterReplyHandler.onRegisterSuccess();
                    }
                }else {
                    if(mRegisterReplyHandler != null) {
                        mRegisterReplyHandler.onRegisterFailure(retstatus, errorInfo);
                    }
                }
            }

        }.execute();
    }

    public void phoneRegister(String phonenum, String passcode)
    {

    }

    public void logout()
    {
        // TODO: 清理本地存储的用户名和密码，切换到登录界面，并且设置切换参数为不自动登录
    }

    private String BuildLoginPostStr(String username, String password, boolean doHash)
    {
        LoginRequest request = new LoginRequest();
        request.setUsername(username);

        if(doHash) {
            request.setPassword(MD5Util.encode(password));
        } else {
            request.setPassword(password);
        }

        String ret = JSON.toJSONString(request);
        Log.d(TAG, ret);

        return ret;
    }

    private String BuildRegisterPostStr(String username, String password)
    {
        RegisterRequest request = new RegisterRequest();
        request.setUsername(username);
        request.setPassword(MD5Util.encode(password));

        String ret = JSON.toJSONString(request);
        return ret;
    }

    // 解析登录请求返回
    private String HandleLoginResult(String loginResponse)
    {
        LoginReply reply = JSON.parseObject(loginResponse, LoginReply.class);

        if(reply != null && reply.getStatus() == 0) {
            SaveLoginReplyData(reply);
            if(initNetworkManager(reply.getServerIp(), reply.getServerPort()) == 0) {
                return "OK";
            }
            else {
                return "Init Network Manager Failed";
            }
        }

        if(reply != null) {
            return reply.getErrorInfo();
        }else {
            return "Null Reply";
        }
    }

    private int HandleRegisterResult(String registerResponse)
    {
        LoginReply reply = JSON.parseObject(registerResponse, LoginReply.class);
        if(reply != null && reply.getStatus() == 0) {
            SaveLoginReplyData(reply);
            if(initNetworkManager(reply.getServerIp(), reply.getServerPort()) == 0) {
                return 0;
            }
            else {
                // TODO: Manager Internal error code
                errorInfo = "Internal Error: Init NetworkManager Failed";
                return -1;
            }
        }

        if(reply != null) {
            errorInfo = reply.getErrorInfo();
            return reply.getStatus();
        }
        // TODO: Error Code
        errorInfo = "Unexpected Error: Empty Reply";
        return -2;
    }

    private String getLoginResult(Common.RESULT result)
    {
        if(result == Common.RESULT.RESULT_SUCCESS) {
            return "OK";
        }
        return result.toString();
    }

    private void HandleLoginSocketError(SocketEvent event)
    {
        if(mLoginReplyHander == null) {
            Log.e(TAG, "LoginReplyHandler not Registered yet");
            return;
        }

        mLoginReplyHander.onSocketEvent(event);
    }

    // 登录成功，在本地保存用户名和密码的MD5，并保存返回的SessionKey用于后续的消息加密
    private void SaveLoginReplyData(LoginReply reply)
    {
        String userId = reply.getUserId();
        ProfilerManager.setUserId(userId);

        String sessionKey = reply.getSessionKey();
        GateKeeper.setSessionKey(sessionKey);
    }

    private int initNetworkManager(String ipStr, int port)
    {
        NetworkManager.getInstance().init(ipStr, port);

        NetworkManager.getInstance().registerConnectHandler(this);

        return NetworkManager.getInstance().connect();
    }


    @Override
    public void connectedCallback() {
        // Connected to server, Send login message
        Client.ClientMsg.Builder clientBuilder = Client.ClientMsg.newBuilder();
        Client.Login.Builder loginBuilder = Client.Login.newBuilder();
        loginBuilder.setSequence(1);
        loginBuilder.setDeviceId("abcdefgh");
        clientBuilder.setLogin(loginBuilder.build());

        NetworkManager.getInstance().sendProtobufMessage(clientBuilder);
    }

    @Override
    public void disconnectedCallback() {

    }

    @Override
    public void timeoutCallback() {

    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        Class cls = msg.obj.getClass();
        if(cls.equals(Server.LoginReply.class)) {
            // Received Login Reply Message
            onServerLoginReply((Server.LoginReply)msg.obj);
        }
    }

    private void onServerLoginReply(Server.LoginReply reply)
    {
        // 检查状态码
        loginResult = reply.getResult();
        if(loginResult == Common.RESULT.RESULT_SUCCESS) {
            // 保存返回的用户信息
            ProfilerManager.getInstance().setUserProfiler(reply.getProfile());
        }
        loginFinished = true;
    }
}
