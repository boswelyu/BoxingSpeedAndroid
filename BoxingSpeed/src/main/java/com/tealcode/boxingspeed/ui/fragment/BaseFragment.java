package com.tealcode.boxingspeed.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tealcode.boxingspeed.R;

/**
 * Created by Boswell Yu on 2017/9/24.
 */

public class BaseFragment extends Fragment {

    protected ViewGroup topContentView;
    protected TextView  topTitleText;

    protected ImageView topLeftButton;
    protected TextView  topLeftText;
    protected ImageView topRightButton;
    protected TextView  topRightText;

    protected RelativeLayout topLeftContainer;
    protected RelativeLayout topRightContainer;

    protected ProgressBar progressBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        topContentView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.fragment_base, null);
        topTitleText = (TextView) topContentView.findViewById(R.id.base_fragment_title);
        topTitleText.setVisibility(View.VISIBLE);

        topLeftButton = (ImageView) topContentView.findViewById(R.id.top_left_image);
        topLeftText = (TextView) topContentView.findViewById(R.id.top_left_text);
        topRightButton = (ImageView) topContentView.findViewById(R.id.top_right_image);
        topRightText = (TextView) topContentView.findViewById(R.id.top_right_text);

        topLeftButton.setVisibility(View.GONE);
        topLeftText.setVisibility(View.GONE);
        topRightButton.setVisibility(View.GONE);
        topRightText.setVisibility(View.GONE);

        topLeftContainer = (RelativeLayout) topContentView.findViewById(R.id.top_left_container);
        topRightContainer = (RelativeLayout) topContentView.findViewById(R.id.top_right_container);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(topContentView != null) {
            ViewGroup parent = (ViewGroup) topContentView.getParent();
            if(parent != null) {
                parent.removeView(topContentView);
            }
        }
        return topContentView;
    }

    public void Init(View currView) {
        progressBar = (ProgressBar)currView.findViewById(R.id.progress_bar);
    }

    public void ShowProgressBar(boolean flag) {
        if(progressBar == null) { return; }
        if(flag) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }
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

    protected void setTopLeftButton(int resID) {
        if (resID <= 0) {
            return;
        }

        topLeftButton.setImageResource(resID);
        topLeftButton.setVisibility(View.VISIBLE);
    }

    protected void setTopLeftText(String text) {
        if (null == text) {
            return;
        }
        topLeftText.setText(text);
        topLeftText.setVisibility(View.VISIBLE);
    }

    protected void hideTopLeftText()
    {
        topLeftText.setVisibility(View.GONE);
    }

    protected void hideTopLeftButton()
    {
        topLeftButton.setVisibility(View.GONE);
    }

    protected void setTopRightButton(int resID) {
        if (resID <= 0) {
            return;
        }

        topRightButton.setImageResource(resID);
        topRightButton.setVisibility(View.VISIBLE);
    }

    protected void setTopRightText(String text) {
        if (null == text) {
            return;
        }
        topRightText.setText(text);
        topRightText.setVisibility(View.VISIBLE);
    }

    protected void hideTopRightButton()
    {
        topRightButton.setVisibility(View.GONE);
    }

    protected void hideTopRightText()
    {
        topRightText.setVisibility(View.GONE);
    }
}
