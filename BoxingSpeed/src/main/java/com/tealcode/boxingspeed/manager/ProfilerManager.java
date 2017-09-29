package com.tealcode.boxingspeed.manager;

import com.tealcode.boxingspeed.helper.entity.UserEntity;

/**
 * Created by YuBo on 2017/9/29.
 */

public class ProfilerManager {
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
}
