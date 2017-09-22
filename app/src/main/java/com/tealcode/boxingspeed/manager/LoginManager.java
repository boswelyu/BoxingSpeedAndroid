package com.tealcode.boxingspeed.manager;

import android.os.AsyncTask;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.tealcode.boxingspeed.config.AppConfig;
import com.tealcode.boxingspeed.message.LoginReply;
import com.tealcode.boxingspeed.message.LoginRequest;
import com.tealcode.boxingspeed.message.SocketEvent;
import com.tealcode.boxingspeed.handler.ILoginReplyHandler;

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

public class LoginManager {
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
    private AsyncTask<Void, Void, Void> loginTask = null;

    public void registerHandler(ILoginReplyHandler handler)
    {
        this.mLoginReplyHander = handler;
    }

    public void login(final String username, final String password)
    {
        if(loginTask != null) {
            loginTask.cancel(true);
        }

        loginTask = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                URL url = null;
                try {
                    url = new URL(AppConfig.LoginServer);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");

                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);

                    PrintWriter printWriter = new PrintWriter(urlConnection.getOutputStream());
                    printWriter.write(BuildLoginPostStr(username, password));
                    printWriter.flush();

                    // Read login response
                    BufferedInputStream bis = new BufferedInputStream(urlConnection.getInputStream());
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();

                    int len;
                    byte[] buffer = new byte[1024];
                    while((len = bis.read(buffer)) != -1)
                    {
                        bos.write(buffer, 0 , len);
                        bos.flush();
                    }
                    bos.close();

                    String loginResult = bos.toString("utf-8");
                    HandleLoginResult(loginResult);

                }catch(MalformedURLException urle) {
                    Log.e(TAG, "LoginURL Malformed");
                    HandleLoginSocketError(SocketEvent.ADDRESS_INVALID);
                }catch(IOException ioe) {
                    HandleLoginSocketError(SocketEvent.ADDRESS_CONNECT_FAILED);
                }

                return null;
            }


        }.execute();


    }

    private String BuildLoginPostStr(String username, String password)
    {
        LoginRequest request = new LoginRequest();
        request.setUsername(username);
        request.setPassword(password);

        return JSON.toJSONString(request);
    }

    // 解析登录请求返回
    private void HandleLoginResult(String loginResponse)
    {
        if(mLoginReplyHander == null) {
            Log.e(TAG, "LoginReplyHandler not registered yet, got login reply: " + loginResponse);
            return;
        }
        LoginReply reply = JSON.parseObject(loginResponse, LoginReply.class);

        if(reply != null && reply.getStatus().equals("OK")) {
            SaveLoginReplyData(reply);
        }

        mLoginReplyHander.onLoginReply(reply);
    }

    private void HandleLoginSocketError(SocketEvent event)
    {
        if(mLoginReplyHander == null) {
            Log.e(TAG, "LoginReplyHandler not Registered yet");
            return;
        }

        mLoginReplyHander.onSocketEvent(event);
    }

    private void SaveLoginReplyData(LoginReply reply)
    {
        // TODO:
    }
}
