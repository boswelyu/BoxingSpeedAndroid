package com.tealcode.boxingspeed.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tealcode.boxingspeed.R;
import com.tealcode.boxingspeed.helper.entity.PeerEntity;
import com.tealcode.boxingspeed.ui.widget.BaseImageView;

import java.util.List;

/**
 * Created by YuBo on 2017/10/16.
 */

public class FriendListAdapter extends BaseAdapter {

    private Context mContext;
    private List<PeerEntity> friendsList;

    public FriendListAdapter(Context context, List<PeerEntity> list)
    {
        this.mContext = context;
        this.friendsList = list;
    }

    @Override
    public int getCount() {
        return this.friendsList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.friendsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FriendCellHolder holder = null;
        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(this.mContext);
            convertView = inflater.inflate(R.layout.widget_friend_cell, null, false);

            holder = new FriendCellHolder();
            holder.headImage = (BaseImageView) convertView.findViewById(R.id.head_image);
            holder.nicknameText = (TextView) convertView.findViewById(R.id.nickname_text);
            holder.signatureText = (TextView) convertView.findViewById(R.id.signature_text);

            PeerEntity peerInfo = (PeerEntity) getItem(position);
            if(peerInfo != null) {
                if(peerInfo.getAvatarUrl() != null) {
                    holder.headImage.loadImageFromUrl(peerInfo.getAvatarUrl());
                } else {
                    holder.headImage.setImageResource(R.drawable.default_user_portrait);
                }

                holder.nicknameText.setText(peerInfo.getNickname());
                holder.signatureText.setText(peerInfo.getSignature());
            }

            convertView.setTag(holder);
        }
        else {
            holder = (FriendCellHolder)convertView.getTag();

            PeerEntity peerInfo = (PeerEntity) getItem(position);
            if(peerInfo != null) {
                if(peerInfo.getAvatarUrl() != null) {
                    holder.headImage.loadImageFromUrl(peerInfo.getAvatarUrl());
                } else {
                    holder.headImage.setImageResource(R.drawable.default_user_portrait);
                }

                holder.nicknameText.setText(peerInfo.getNickname());
                holder.signatureText.setText(peerInfo.getSignature());
            }

        }
        return convertView;
    }

    private static class FriendCellHolder
    {
        BaseImageView headImage;
        TextView  nicknameText;
        TextView  signatureText;
    }
}
