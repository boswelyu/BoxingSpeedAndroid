package com.tealcode.boxingspeed.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.tealcode.boxingspeed.R;

/**
 * Created by YuBo on 2017/9/29.
 */

public class UserInfoActivity extends FragmentActivity {

    private View mProgressView;
    private TextView mprogressText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);

        mProgressView = findViewById(R.id.progress_bar);
        mprogressText = (TextView) findViewById(R.id.progress_message);
    }

    public void showProgress(boolean flag)
    {
        if(flag) {
            mProgressView.setVisibility(View.VISIBLE);
        }
        else {
            mProgressView.setVisibility(View.GONE);
        }
    }

    public void setProgressText(String text)
    {
        mProgressView.setVisibility(View.VISIBLE);
        mprogressText.setText(text);
    }
}
