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

public class ProfilerFragment extends BaseFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profiler, null);
    }
}
