package com.tealcode.boxingspeed.manager;

import android.util.Log;

import com.tealcode.boxingspeed.helper.entity.UserEntity;

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

    private UserEntity mUserEntity;

    public void setUserEntity(UserEntity ue) {
        this.mUserEntity = ue;
    }

    public UserEntity getUserEntity()
    {
        return this.mUserEntity;
    }

    public static int getUserId()
    {
        if(instance == null) {
            Log.e(TAG, "ProfilerManager Instance Not Created Yet");
            return 0;
        }

        UserEntity entity = instance.getUserEntity();
        if(entity == null) {
            Log.e(TAG, "User Entity Not Set yet");
            return 0;
        }
        return entity.getUserId();
    }


}
