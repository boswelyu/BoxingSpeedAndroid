package com.tealcode.boxingspeed.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tealcode.boxingspeed.R;

/**
 * Created by Boswell Yu on 2017/9/24.
 */

public class BoxingFragment extends BaseFragment {

    private View mCurrView = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mCurrView = inflater.inflate(R.layout.fragment_boxing, topContentView);
        setTopTitleBold(getString(R.string.main_boxing));
        return mCurrView;
    }
}
