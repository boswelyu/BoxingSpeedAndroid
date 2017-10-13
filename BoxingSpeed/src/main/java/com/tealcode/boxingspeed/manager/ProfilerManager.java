package com.tealcode.boxingspeed.manager;

import android.util.Log;

import com.tealcode.boxingspeed.helper.entity.UserEntity;
import com.tealcode.boxingspeed.protobuf.Server;

/**
 * Created by YuBo on 2017/9/29.
 */

public class ProfilerManager {

    private static final String TAG = "ProfilerManager";
    private static ProfilerManager instance = null;
    public static ProfilerManager getInstance() {
        if(instance == null) {
            instance = new ProfilerManager();
        }
        return instance;
    }

    private static int userId = 0;

    private Server.UserProfile mUserProfiler;

    public void setUserProfiler(Server.UserProfile prof) {
        this.mUserProfiler = prof;

        int uid = prof.getUserId();
        if(uid != userId) {
            Log.e(TAG, "Why I Got other one's userId: " + uid);
        }
    }

    public Server.UserProfile getUserProfiler()
    {
        return mUserProfiler;
    }

    public static void setUserId(String uid)
    {
        userId = Integer.parseInt(uid);
    }

    public static int getUserId()
    {
        return userId;
    }

}
