package com.tealcode.boxingspeed.ui.widget;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;

import com.tealcode.boxingspeed.R;
import com.tealcode.boxingspeed.helper.HttpImageLoader;

/**
 * Created by YuBo on 2017/9/27.
 */

public class BaseImageView extends AppCompatImageView {

    private static final String TAG = "BaseImageView";

    protected String imageUrl = null;
    protected boolean attachedOnWindow = false;
    protected int defaultImageResId = R.drawable.default_user_portrait;

    public BaseImageView(Context context) {
        super(context);
    }

    public BaseImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void LoadImageFromUrl(String url)
    {
        if(this.imageUrl.equals(url)) {
            // Already loaded the same image
            Log.d(TAG, "Already loaded the same image: " + url);
            return;
        }

        this.imageUrl = url;
        HttpImageLoader.getLoaderInstance().displayImage(url, this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        attachedOnWindow = true;
        if(this.imageUrl != null) {
            LoadImageFromUrl(this.imageUrl);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.attachedOnWindow = false;
        HttpImageLoader.getLoaderInstance().cancelDisplayTask(this);
    }
}
