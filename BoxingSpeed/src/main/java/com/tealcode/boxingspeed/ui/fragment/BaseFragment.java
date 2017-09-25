package com.tealcode.boxingspeed.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tealcode.boxingspeed.R;

/**
 * Created by Boswell Yu on 2017/9/24.
 */

public class BaseFragment extends Fragment {

    protected ViewGroup topContentView;
    protected TextView  topTitleText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        topContentView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.fragment_base, null);
        topTitleText = (TextView) topContentView.findViewById(R.id.base_fragment_title);
        topTitleText.setVisibility(View.VISIBLE);
    }

    protected void setTopTitleBold(String title) {
        if (title == null) {
            return;
        }
        if (title.length() > 12) {
            title = title.substring(0, 11) + "...";
        }
        // 设置字体为加粗
        TextPaint paint =  topTitleText.getPaint();
        paint.setFakeBoldText(true);

        topTitleText.setText(title);
        topTitleText.setVisibility(View.VISIBLE);

    }

    protected void setTopTitle(String title) {
        if (title == null) {
            return;
        }
        if (title.length() > 12) {
            title = title.substring(0, 11) + "...";
        }
        topTitleText.setText(title);
        topTitleText.setVisibility(View.VISIBLE);
    }
}
