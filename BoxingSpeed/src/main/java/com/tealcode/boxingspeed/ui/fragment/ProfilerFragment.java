package com.tealcode.boxingspeed.ui.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.tealcode.boxingspeed.R;
import com.tealcode.boxingspeed.manager.LoginManager;

/**
 * Created by Boswell Yu on 2017/9/24.
 */

public class ProfilerFragment extends BaseFragment {

    private View currFragment = null;

    private View personalView = null;
    private View settingView = null;
    private View clearCacheView = null;
    private View logoutView = null;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(currFragment != null) {
            ViewGroup parent = (ViewGroup)currFragment.getParent();
            if(parent != null) {
                parent.removeView(currFragment);
            }
            return currFragment;
        }
        currFragment = inflater.inflate(R.layout.fragment_profiler, topContentView);
        InitProfiler();

        ShowProgressBar(false);
        return currFragment;
    }

    private void InitProfiler()
    {
        super.Init(currFragment);
        personalView = currFragment.findViewById(R.id.personal_view);

        settingView = currFragment.findViewById(R.id.setting_view);
        clearCacheView = currFragment.findViewById(R.id.clear_cache_view);
        logoutView = currFragment.findViewById(R.id.logout_view);

        // 设置界面
        settingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(ProfilerFragment.this.getActivity(), SettingActivity.class));
            }
        });

        // 清理图片缓存响应事件
        clearCacheView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 提示确定清理本地图片缓存
            }
        });

        // 退出当前账号响应事件
        logoutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder =new
                        AlertDialog.Builder(new android.view.ContextThemeWrapper(getActivity(),
                                android.R.style.Theme_Holo_Light_Dialog));
                LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View dialogView = inflater.inflate(R.layout.popup_dialog, null);
                final EditText contentText = (EditText) dialogView.findViewById(R.id.dialog_content);
                contentText.setVisibility(View.GONE);

                TextView titleText = (TextView) dialogView.findViewById(R.id.dialog_title);
                titleText.setText(R.string.confirm_logout_tip);
                builder.setView(dialogView);
                builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LoginManager.getInstance().logout();
                        getActivity().finish();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

    }
}
