package com.tealcode.boxingspeed.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tealcode.boxingspeed.R;
import com.tealcode.boxingspeed.helper.AppConstant;
import com.tealcode.boxingspeed.manager.ProfilerManager;
import com.tealcode.boxingspeed.ui.widget.BaseImageView;

/**
 * Created by YuBo on 2017/9/29.
 */

public class UserInfoFragment extends BaseFragment {

    private static final String TAG = "UserInfoFragment";

    private View mCurrView = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if(mCurrView != null) {
            ViewGroup parent = (ViewGroup) mCurrView.getParent();
            if(parent != null) {
                parent.removeView(mCurrView);
            }
        } else {
            mCurrView = inflater.inflate(R.layout.fragment_userinfo, topContentView);
        }

        initTopView();
        initLayout();
        initUserInfo();

        return mCurrView;
    }

    private void initTopView()
    {
        setTopTitleBold(getString(R.string.detail_user_info));
        setTopLeftButton(R.drawable.top_back);
        setTopLeftText(getString(R.string.top_left_back));
        topLeftContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO: Check if user changed anything

                // 返回到上个Activity
                getActivity().finish();
            }
        });
    }

    private void initLayout()
    {

    }

    private boolean initUserInfo()
    {
        Intent intent = getActivity().getIntent();
        if(intent == null) {
            Log.e(TAG, "No Input Intent Found");
            return false;
        }

        int userid = intent.getIntExtra(AppConstant.KEY_INTENT_USERID, 0);
        if(userid == 0) {
            Log.e(TAG, "Invalid userid was given");
            return false;
        }

        if(userid == ProfilerManager.getUserId()) {
            // 显示自己的信息，部分字段可以编辑
            displaySelfInfo();
        } else
        {
            // 显示他人的详细信息，所有字段不可编辑
            displayPeerInfo(userid);
        }
        return true;
    }

    // 显示自己的详细信息
    private void displaySelfInfo()
    {

    }

    // 显示他人的信息
    private void displayPeerInfo(int userid)
    {

    }

}
