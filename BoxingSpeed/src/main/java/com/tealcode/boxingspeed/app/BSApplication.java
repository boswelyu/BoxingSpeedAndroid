package com.tealcode.boxingspeed.app;

import android.app.Application;
import android.util.Log;

import com.tealcode.boxingspeed.helper.LocalStorage;

/**
 * Created by YuBo on 2017/9/20.
 */

public class BSApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("BSApplication", "BSApplication onCreate");

        LocalStorage.init(getApplicationContext());
    }
}
