package com.tealcode.boxingspeed.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tealcode.boxingspeed.R;

/**
 * Created by YuBo on 2017/10/13.
 */

public class ArenaFragment extends BaseFragment {

    private static final String TAG = "ArenaFragment";

    private View mCurrView = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(mCurrView != null) {
            ViewGroup parent = (ViewGroup) mCurrView.getParent();
            if(parent != null) {
                parent.removeView(mCurrView);
            }
            Log.d(TAG, "mCurrView Already Created Before");
            return mCurrView;
        }

        mCurrView = inflater.inflate(R.layout.fragment_arena, topContentView);
        setTopTitleBold(getString(R.string.main_arena));

        hideTopLeftButton();
        hideTopLeftText();
        hideTopRightButton();
        hideTopRightText();

        initListeners();

        return mCurrView;
    }

    // 初始化各组件的响应事件
    private void initListeners()
    {

    }
}
