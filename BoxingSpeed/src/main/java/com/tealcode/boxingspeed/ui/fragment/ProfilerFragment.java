package com.tealcode.boxingspeed.ui.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tealcode.boxingspeed.R;
import com.tealcode.boxingspeed.helper.http.HttpImageLoader;
import com.tealcode.boxingspeed.manager.ActivityManager;
import com.tealcode.boxingspeed.manager.LoginManager;
import com.tealcode.boxingspeed.manager.ProfilerManager;
import com.tealcode.boxingspeed.protobuf.Server;
import com.tealcode.boxingspeed.ui.widget.BaseImageView;



/**
 * Created by Boswell Yu on 2017/9/24.
 */

public class ProfilerFragment extends BaseFragment {

    private static final String TAG = "ProfilerFragment";

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

        // 基本信息界面
        initPersonalView();

        // 注册设置界面响应函数
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
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getString(R.string.confirm_clear_cache));
                builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Clear Image Cache
                        HttpImageLoader.ClearCache();
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

        // 退出当前账号响应事件
        logoutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder =new AlertDialog.Builder(getActivity());

                builder.setTitle(getString(R.string.confirm_logout_tip));
                builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LoginManager.getInstance().logout(getActivity());
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

    private void initPersonalView()
    {
        TextView nicknameView = (TextView) personalView.findViewById(R.id.nickname_text);
        TextView usernameView = (TextView) personalView.findViewById(R.id.username_text);
        BaseImageView portraitImageView = (BaseImageView) personalView.findViewById(R.id.user_portrait);

        Server.UserProfile profiler = ProfilerManager.getInstance().getUserProfiler();
        if(profiler == null) {
            Log.e(TAG, "Invalid User Profiler Exist!");
        }

        nicknameView.setText(profiler.getNickname());
        usernameView.setText(profiler.getUsername());

        String avatarUrl = profiler.getAvatarUrl();
        if(avatarUrl == null || avatarUrl.isEmpty()) {
            portraitImageView.setImageResource(R.drawable.default_user_portrait);
        } else {
            portraitImageView.loadImageFromUrl(avatarUrl);
        }

        RelativeLayout container = (RelativeLayout) personalView.findViewById(R.id.user_container);
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Open Profiler Activity
                ActivityManager.startUserInfoActivity(getContext(), ProfilerManager.getUserId());
            }
        });

    }
}
