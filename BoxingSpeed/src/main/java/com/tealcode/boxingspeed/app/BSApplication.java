package com.tealcode.boxingspeed.app;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.mob.MobSDK;
import com.mob.commons.SMSSDK;
import com.tealcode.boxingspeed.helper.GateKeeper;
import com.tealcode.boxingspeed.helper.http.HttpImageLoader;
import com.tealcode.boxingspeed.helper.LocalStorage;

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

        GateKeeper.init();

        MobSDK.init(getApplicationContext(), "21e1ede9448b0", "adb5396bd8dba46b6fc8c9a0b6228c20");
    }
}
