package com.tealcode.boxingspeed.ui.widget;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.tealcode.boxingspeed.R;

/**
 * Created by YuBo on 2017/9/27.
 */

public class BaseImageView extends AppCompatImageView {

    protected String imageUrl = null;
    protected int imageId;
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


}
