package com.tealcode.boxingspeed.manager;

import android.util.Log;

import com.tealcode.boxingspeed.helper.entity.PeerEntity;
import com.tealcode.boxingspeed.helper.entity.UserEntity;
import com.tealcode.boxingspeed.protobuf.Server;

import java.util.ArrayList;
import java.util.List;

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
    private List<PeerEntity>   mFriendsList;

    public void setUserProfiler(Server.UserProfile prof) {
        this.mUserProfiler = prof;

        int uid = prof.getUserId();
        if(uid != userId) {
            Log.e(TAG, "Why I Got other one's userId: " + uid);
        }

        // TODO: 根据返回的好友信息初始化好友列表
        mFriendsList = new ArrayList<>();
        PeerEntity entity = new PeerEntity();
        entity.setUsername("wangfang");
        entity.setNickname("beauty");
        entity.setAge(29);
        entity.setGender("female");
        entity.setSignature("I say I do");
        entity.setAvatarUrl("http://img3.duitang.com/uploads/item/201506/27/20150627151623_FJika.png");
        mFriendsList.add(entity);

        entity = new PeerEntity();
        entity.setUsername("peipei");
        entity.setNickname("Cool Girl");
        entity.setAge(29);
        entity.setGender("female");
        entity.setSignature("Me is Me");
        entity.setAvatarUrl("http://img2.imgtn.bdimg.com/it/u=777760294,1096287853&fm=214&gp=0.jpg");
        mFriendsList.add(entity);

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

    public List<PeerEntity> getFriendsList()
    {
        return this.mFriendsList;
    }

}
