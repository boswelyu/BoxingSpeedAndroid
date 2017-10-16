package com.tealcode.boxingspeed.ui.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.tealcode.boxingspeed.R;

import java.util.zip.Inflater;

/**
 * Created by YuBo on 2017/10/16.
 */

public class FriendButton extends FrameLayout {

    private Context mContext;
    private View    mCurrView;

    private ImageView mButtonImage;
    private TextView mNoticeImage;

    public FriendButton(Context context) {
        this(context, null);
    }

    public FriendButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FriendButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.mContext = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mCurrView = inflater.inflate(R.layout.widget_friend_button, this, true);

        mButtonImage = (ImageView) mCurrView.findViewById(R.id.friend_button_image);
        mNoticeImage = (TextView) mCurrView.findViewById(R.id.friend_notice_image);
    }

    public void setNoticeNumber(int number)
    {
        if(number == 0) {
            mNoticeImage.setVisibility(View.INVISIBLE);
            return;
        }

        String notice = "";
        if(number > 99) {
            notice = "99+";
        }else {
            notice = Integer.toString(number);
        }

        mNoticeImage.setText(notice);
        mNoticeImage.setVisibility(View.VISIBLE);
    }


}
