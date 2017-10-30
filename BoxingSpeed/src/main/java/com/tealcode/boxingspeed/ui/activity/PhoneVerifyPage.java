package com.tealcode.boxingspeed.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.tealcode.boxingspeed.handler.PhoneVerifyHandler;

/**
 * Created by YuBo on 2017/10/26.
 */

public class PhoneVerifyPage extends Activity {

    private PhoneVerifyHandler mHandler = null;

    // Public method
    public void registerEventHandler(PhoneVerifyHandler handler)
    {
        mHandler = handler;
    }

    public void show(Context context)
    {
        Intent intent = new Intent(context, PhoneVerifyPage.class);
        startActivity(intent);
    }
}
