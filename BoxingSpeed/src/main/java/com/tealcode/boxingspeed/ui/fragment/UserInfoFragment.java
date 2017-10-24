package com.tealcode.boxingspeed.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceActivity;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.tealcode.boxingspeed.ui.activity.CropImageActivity;
import com.tealcode.boxingspeed.ui.widget.BaseImageView;
import com.tealcode.boxingspeed.utility.ImageUtil;

import java.io.ByteArrayOutputStream;

import cz.msebera.android.httpclient.Header;


/**
 * Created by YuBo on 2017/9/29.
 */

public class UserInfoFragment extends BaseFragment {

    private static final String TAG = "UserInfoFragment";

    private static UserInfoFragment instance = null;

    public static UserInfoFragment getInstance() {
        return instance;
    }

    private View mCurrView = null;

    private BaseImageView mPortrait;
    private ImageView mGenderImage;
    private TextView mUsernameText;
    private TextView mNicknameText;
    private TableRow mPhoneNumberRow;
    private TextView mPhoneNumberText;
    private TextView mBirthText;
    private TextView mLocationText;
    private TextView mSignatureText;
    private Button   mCommandButton;

    private InputMethodManager inputManager;

    // 定义请求代码
    private static final int REQUEST_GALLERY = 1;
    private static final int REQUEST_CAMERA = 2;
    private static final int REQUEST_CROP = 3;

    // 记录是否有信息的变化
    private boolean hasChanged = false;

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

        inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        initTopView();
        initLayout();
        initUserInfo();

        instance = this;

        return mCurrView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        instance = null;
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
        mGenderImage = (ImageView) mCurrView.findViewById(R.id.userinfo_gender);
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

        // Username不可修改
        // Nickname不可修改

        mPhoneNumberText.setInputType(InputType.TYPE_CLASS_PHONE);


        hasChanged = false;
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
                Bitmap gBitmap = ImageUtil.getBitmapFromUri(getActivity(), uri);
                cropBitmap(gBitmap);
                break;
            case REQUEST_CAMERA:
                Uri curi = data.getData();
                Bitmap cBitmap = ImageUtil.getBitmapFromUri(getActivity(), curi);
                cropBitmap(cBitmap);
                break;

            case REQUEST_CROP:
                // 从裁剪图片界面返回
                Uri avatar_uri = data.getData();
                Bitmap avatar = ImageUtil.getBitmapFromUri(getActivity(), avatar_uri);
                setAvatarImage(avatar);
                break;

        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    private void cropBitmap(Bitmap bitmap)
    {
        Uri sourceUri = ImageUtil.saveBitmapToUri(getActivity(), bitmap, "upload", "avatar.png");

        Intent intent = new Intent(getContext(), CropImageActivity.class);
        intent.setData(sourceUri);

        startActivityForResult(intent, REQUEST_CROP);
    }


    private void setAvatarImage(Bitmap bitmap)
    {
        mPortrait.setImageBitmap(bitmap);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bytes = stream.toByteArray();
        String img = new String(Base64.encodeToString(bytes, Base64.DEFAULT));

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("type", "avatar");
        params.put("user_id", Integer.toString(ProfilerManager.getUserId()));
//        params.put("image", img);
        client.post(AppConfig.UploadAvatarUrl, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });


    }
}
