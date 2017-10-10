package com.tealcode.boxingspeed.app;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.tealcode.boxingspeed.config.AppConfig;
import com.tealcode.boxingspeed.helper.HttpImageLoader;
import com.tealcode.boxingspeed.helper.LocalStorage;
import com.tealcode.boxingspeed.manager.NetworkManager;

/**
 * Created by YuBo on 2017/9/20.
 */

public class BSApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("BSApplication", "BSApplication onCreate");
        Context appContext = getApplicationContext();
        LocalStorage.init(appContext);
        HttpImageLoader.InitConfig(appContext);

        NetworkManager.getInstance().init(AppConfig.SocketServerIp, AppConfig.SocketServerPort);
        NetworkManager.getInstance().connect();
    }
}
