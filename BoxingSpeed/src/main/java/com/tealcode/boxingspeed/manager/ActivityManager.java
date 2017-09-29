package com.tealcode.boxingspeed.manager;

import android.content.Context;
import android.content.Intent;

import com.tealcode.boxingspeed.helper.AppConstant;
import com.tealcode.boxingspeed.ui.activity.UserInfoActivity;

/**
 * Created by YuBo on 2017/9/29.
 */

public class ActivityManager {

    public static void startUserInfoActivity(Context context, int peerid)
    {
        Intent intent = new Intent(context, UserInfoActivity.class);
        intent.putExtra(AppConstant.INTENT_KEY_PEERID, peerid);
        context.startActivity(intent);
    }
}
