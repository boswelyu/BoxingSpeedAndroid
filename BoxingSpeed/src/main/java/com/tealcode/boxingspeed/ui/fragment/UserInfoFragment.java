package com.tealcode.boxingspeed.ui.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.Base64;
import com.loopj.android.http.RequestParams;
import com.tealcode.boxingspeed.R;
import com.tealcode.boxingspeed.config.AppConfig;
import com.tealcode.boxingspeed.helper.AppConstant;
import com.tealcode.boxingspeed.manager.ProfilerManager;
import com.tealcode.boxingspeed.protobuf.Server;
import com.tealcode.boxingspeed.ui.widget.BaseImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cz.msebera.android.httpclient.Header;

/**
 * Created by YuBo on 2017/9/29.
 */

public class UserInfoFragment extends BaseFragment {

    private static final String TAG = "UserInfoFragment";

    private View mCurrView = null;

    private BaseImageView mPortrait;
    private ImageButton mGenderImage;
    private TextView mUsernameText;
    private TextView mNicknameText;
    private TableRow mPhoneNumberRow;
    private TextView mPhoneNumberText;
    private TextView mBirthText;
    private TextView mLocationText;
    private TextView mSignatureText;
    private Button   mCommandButton;

    // 定义请求代码
    private static final int REQUEST_GALLERY = 1;
    private static final int REQUEST_CAMERA = 2;
    private static final int REQUEST_CROP = 3;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if(mCurrView != null) {
            ViewGroup parent = (ViewGroup) mCurrView.getParent();
            if(parent != null) {
                parent.removeView(mCurrView);
            }
        } else {
            mCurrView = inflater.inflate(R.layout.fragment_userinfo, topContentView);
        }

        initTopView();
        initLayout();
        initUserInfo();

        return mCurrView;
    }

    private void initTopView()
    {
        setTopTitleBold(getString(R.string.detail_user_info));
        setTopLeftButton(R.drawable.top_back);
        setTopLeftText(getString(R.string.top_left_back));
        topLeftContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO: Check if user changed anything

                // 返回到上个Activity
                getActivity().finish();
            }
        });
    }

    private void initLayout()
    {
        mPortrait = (BaseImageView) mCurrView.findViewById(R.id.userinfo_portrait);
        mGenderImage = (ImageButton) mCurrView.findViewById(R.id.userinfo_gender);
        mUsernameText = (TextView) mCurrView.findViewById(R.id.userinfo_nickname);
        mNicknameText = (TextView) mCurrView.findViewById(R.id.userinfo_nickname);
        mPhoneNumberRow = (TableRow) mCurrView.findViewById(R.id.userinfo_phone_row);
        mPhoneNumberText = (TextView) mCurrView.findViewById(R.id.userinfo_phone_number);
        mBirthText = (TextView) mCurrView.findViewById(R.id.userinfo_birth);
        mLocationText = (TextView) mCurrView.findViewById(R.id.userinfo_location);
        mSignatureText = (TextView) mCurrView.findViewById(R.id.userinfo_signature);
        mCommandButton = (Button) mCurrView.findViewById(R.id.userinfo_command);
;    }

    private boolean initUserInfo()
    {
        Intent intent = getActivity().getIntent();
        if(intent == null) {
            Log.e(TAG, "No Input Intent Found");
            return false;
        }

        int userid = intent.getIntExtra(AppConstant.KEY_INTENT_USERID, 0);
        if(userid == 0) {
            Log.e(TAG, "Invalid userid was given");
            return false;
        }

        if(userid == ProfilerManager.getUserId()) {
            // 显示自己的信息，部分字段可以编辑
            displaySelfInfo();
        } else
        {
            // 显示他人的详细信息，所有字段不可编辑
            displayPeerInfo(userid);
        }
        return true;
    }

    // 显示自己的详细信息
    private void displaySelfInfo()
    {
        Server.UserProfile profile = ProfilerManager.getInstance().getUserProfiler();

        String avatarUrl = profile.getAvatarUrl();
        if(avatarUrl == null || avatarUrl.isEmpty()) {
            mPortrait.setImageResource(R.drawable.default_user_portrait);
        } else {
            mPortrait.loadImageFromUrl(avatarUrl);
        }

        int gender = profile.getGender();
        if(gender == 0) {
            mGenderImage.setImageResource(R.drawable.gender_female);
        } else {
            mGenderImage.setImageResource(R.drawable.gender_male);
        }

        mUsernameText.setText(profile.getUsername());
        mNicknameText.setText(profile.getNickname());
//        mBirthText.setText(profile.getBirthDate());
//        mPhoneNumberText.setText(profile.getPhoneNumber());
//        mLocationText.setText(profile.getLocation());
        mSignatureText.setText(profile.getSignature());

        // 显示自己的信息，隐藏最底部的操作按钮
        mCommandButton.setVisibility(View.GONE);

        // 头像可修改
        mPortrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 打开图库选择文件并剪切
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_GALLERY);
            }
        });
    }

    // 显示他人的信息
    private void displayPeerInfo(int userid)
    {
        mPhoneNumberRow.setVisibility(View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data == null) {
            // No valid action
            return;
        }

        switch(requestCode) {
            case REQUEST_GALLERY:
                // 从相册选择中返回
                Uri uri = data.getData();
                cropImage(uri);
                break;
            case REQUEST_CAMERA:
                Bitmap bitmap = data.getParcelableExtra("data");
                Uri capturedImageUri = saveAvatarBitmap(bitmap);
                cropImage(capturedImageUri);
                break;

            case REQUEST_CROP:
                // 从裁剪图片界面返回
                Bitmap avatar = data.getParcelableExtra("data");
                setAvatarImage(avatar);
                break;

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void cropImage(Uri imgUri)
    {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(imgUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 160);
        intent.putExtra("outputY", 160);
        intent.putExtra("return-data", true);

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        startActivityForResult(intent, REQUEST_CROP);
    }

    //
    private Uri saveAvatarBitmap(Bitmap bitmap)
    {
        File tempDir = new File(Environment.getExternalStorageDirectory() + "/com.tealcode.boxingspeed");
        if(!tempDir.exists()) {
            tempDir.mkdir();
        }

        File img = new File(tempDir.getAbsolutePath() + "avatar.png");
        try {
            FileOutputStream fos = new FileOutputStream(img);
            bitmap.compress(Bitmap.CompressFormat.PNG, 85, fos);
            fos.flush();
            fos.close();
            return Uri.fromFile(img);
        } catch (Exception e) {
            Log.e(TAG, "SaveAvatarBitmap Failed with exception: " + e.toString());
            return null;
        }
    }

    private void setAvatarImage(Bitmap bitmap)
    {
        mPortrait.setImageBitmap(bitmap);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 60, stream);
        byte[] bytes = stream.toByteArray();
        String img = new String(Base64.encodeToString(bytes, Base64.DEFAULT));

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("type", "avatar");
        params.add("user_id", Integer.toString(ProfilerManager.getUserId()));
        params.add("image", img);
        client.post(AppConfig.UploadAvatarUrl, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast.makeText(getContext(), getString(R.string.upload_avatar_success), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), getString(R.string.upload_avatar_failed), Toast.LENGTH_SHORT).show();
            }
        });


    }
}
