package com.tealcode.boxingspeed.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tealcode.boxingspeed.R;

/**
 * Created by YuBo on 2017/9/29.
 */

public class UserInfoFragment extends BaseFragment {

    private View currView = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(currView != null) {
            ViewGroup parent = (ViewGroup) currView.getParent();
            if(parent != null) {
                parent.removeView(currView);
            }
            return currView;
        }
        currView = inflater.inflate(R.layout.fragment_userinfo, topContentView);
        ShowProgressBar(true);
        initTopView();
        return currView;
    }

    private void initTopView()
    {
        setTopTitleBold(getString(R.string.detail_user_info));
        setTopLeftButton(R.drawable.top_back);
        setTopLeftText(getString(R.string.top_left_back));
        topLeftContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 返回到上个Activity
                getActivity().finish();
            }
        });
    }

}
