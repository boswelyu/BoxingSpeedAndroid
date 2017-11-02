package com.tealcode.boxingspeed.manager;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.tealcode.boxingspeed.config.AppConfig;
import com.tealcode.boxingspeed.handler.IConnectHandler;
import com.tealcode.boxingspeed.handler.IRegisterReplyHandler;
import com.tealcode.boxingspeed.helper.AppConstant;
import com.tealcode.boxingspeed.helper.GateKeeper;
import com.tealcode.boxingspeed.helper.LocalStorage;
import com.tealcode.boxingspeed.helper.http.AsyncHttpReplyHandler;
import com.tealcode.boxingspeed.helper.http.AsyncHttpWorker;
import com.tealcode.boxingspeed.message.LoginReply;
import com.tealcode.boxingspeed.message.LoginRequest;
import com.tealcode.boxingspeed.message.RegisterRequest;
import com.tealcode.boxingspeed.message.SocketEvent;
import com.tealcode.boxingspeed.handler.ILoginReplyHandler;
import com.tealcode.boxingspeed.protobuf.Client;
import com.tealcode.boxingspeed.protobuf.Common;
import com.tealcode.boxingspeed.protobuf.Server;
import com.tealcode.boxingspeed.ui.activity.LoginActivity;
import com.tealcode.boxingspeed.utility.AES;
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

    private String mUsername;
    private String mPassowrdMd5;

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
                    urlConnection.setConnectTimeout(2000);
                    urlConnection.setReadTimeout(3000);
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
                    Log.e(TAG, "Failed to connect to server");
                    HandleLoginSocketError(SocketEvent.ADDRESS_CONNECT_FAILED);
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
                    // 保存当前登录用的用户名和密码到本地存储
                    LocalStorage.getInstance().setStringValue(AppConstant.KEY_SAVED_USERNAME, mUsername);
                    LocalStorage.getInstance().setStringValue(AppConstant.KEY_SAVED_PASSWORD, mPassowrdMd5);

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

        String postStr = BuildRegisterPostStr(username, password);
        AsyncHttpWorker httpWorker = new AsyncHttpWorker();
        httpWorker.post(AppConfig.RegisterUrl, postStr, new AsyncHttpReplyHandler() {
            @Override
            public void onSuccess(String replyStr) {
                LoginReply reply = JSON.parseObject(replyStr, LoginReply.class);

                boolean hasError = false;

                if(reply != null && reply.getStatus() == 0) {
                    SaveLoginReplyData(reply);
                    if(AES.init(reply.getSessionKey()) != 0)
                    {
                        errorInfo = "Init AES Failed";
                        hasError = true;
                    }

                    if(initNetworkManager(reply.getServerIp(), reply.getServerPort()) != 0) {
                        errorInfo = "Internal Error: Init NetworkManager Failed";
                        hasError = true;
                    }
                } else {
                    errorInfo = "Internal Error: Reply empty";
                    hasError = true;
                }

                if(!hasError) {
                    if(mRegisterReplyHandler != null) {
                        mRegisterReplyHandler.onRegisterSuccess();
                    }
                }
                else {
                    if(mRegisterReplyHandler != null) {
                        mRegisterReplyHandler.onRegisterFailure(-1, errorInfo);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, String errorInfo) {
                if(mRegisterReplyHandler != null) {
                    mRegisterReplyHandler.onRegisterFailure(statusCode, errorInfo);
                }
            }
        });


    }

    public void phoneRegister(String phonenum, String passcode)
    {

    }

    public void logout(Context context)
    {
        // 清理本地存储的用户名和密码，
        LocalStorage.getInstance().setStringValue(AppConstant.KEY_SAVED_USERNAME, "");
        LocalStorage.getInstance().setStringValue(AppConstant.KEY_SAVED_PASSWORD, "");

        // 切换到登录界面，并且设置切换参数为不自动登录
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(AppConstant.KEY_AUTO_LOGIN, false);
        context.startActivity(intent);
    }

    private String BuildLoginPostStr(String username, String password, boolean doHash)
    {
        LoginRequest request = new LoginRequest();
        request.setUsername(username);

        this.mUsername = username;

        if(doHash) {
            String hashPw = MD5Util.encode(password);
            request.setPassword(hashPw);
            this.mPassowrdMd5 = hashPw;
        } else {
            request.setPassword(password);
            this.mPassowrdMd5 = password;
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

            if(AES.init(reply.getSessionKey()) != 0) {
                return "AES Init Failed";
            }

            if(initNetworkManager(reply.getServerIp(), reply.getServerPort()) != 0) {
                return "Init Network Manager Failed";
            }

            return "OK";
        }

        if(reply != null) {
            return reply.getErrorInfo();
        }else {
            return "Null Reply";
        }
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
