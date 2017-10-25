package com.tealcode.boxingspeed.ui.fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.input.InputManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.lljjcoder.city_20170724.CityPickerView;
import com.lljjcoder.city_20170724.bean.CityBean;
import com.lljjcoder.city_20170724.bean.DistrictBean;
import com.lljjcoder.city_20170724.bean.ProvinceBean;
import com.tealcode.boxingspeed.R;
import com.tealcode.boxingspeed.config.AppConfig;
import com.tealcode.boxingspeed.helper.AppConstant;
import com.tealcode.boxingspeed.helper.http.AsyncHttpReplyHandler;
import com.tealcode.boxingspeed.helper.http.AsyncHttpWorker;
import com.tealcode.boxingspeed.manager.ProfilerManager;
import com.tealcode.boxingspeed.message.UploadRequest;
import com.tealcode.boxingspeed.protobuf.Server;
import com.tealcode.boxingspeed.ui.activity.CropImageActivity;
import com.tealcode.boxingspeed.ui.widget.BaseImageView;
import com.tealcode.boxingspeed.utility.ImageUtil;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.smssdk.gui.IdentifyNumPage;


/**
 * Created by YuBo on 2017/9/29.
 */

public class UserInfoFragment extends BaseFragment {

    private static final String TAG = "UserInfoFragment";

    private static UserInfoFragment instance = null;

    public static UserInfoFragment getInstance() {
        return instance;
    }

    private InputMethodManager inputManager = null;

    private View mCurrView = null;

    private BaseImageView mPortrait;
    private ImageView mGenderImage;
    private TextView mUsernameText;
    private TextView mNicknameText;
    private TableRow mPhoneNumberRow;
    private EditText mPhoneNumberText;
    private TextView mBirthDateText;
    private TextView mLocationText;
    private EditText mSignatureText;
    private Button   mCommandButton;

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

        inputManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

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

        // 保存修改
        topRightContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 保存修改
                if(hasChanged) {
                    // TODO: 发送新的profile信息到服务器
                }

                hasChanged = false;
            }
        });
    }

    private void initLayout()
    {
        mPortrait = (BaseImageView) mCurrView.findViewById(R.id.userinfo_portrait);
        mGenderImage = (ImageView) mCurrView.findViewById(R.id.userinfo_gender);
        mUsernameText = (TextView) mCurrView.findViewById(R.id.userinfo_username);
        mNicknameText = (TextView) mCurrView.findViewById(R.id.userinfo_nickname);
        mPhoneNumberRow = (TableRow) mCurrView.findViewById(R.id.userinfo_phone_row);
        mPhoneNumberText = (EditText) mCurrView.findViewById(R.id.userinfo_phone_number);
        mBirthDateText = (TextView) mCurrView.findViewById(R.id.userinfo_birth);
        mLocationText = (TextView) mCurrView.findViewById(R.id.userinfo_location);
        mSignatureText = (EditText) mCurrView.findViewById(R.id.userinfo_signature);
        mCommandButton = (Button) mCurrView.findViewById(R.id.userinfo_command);

        mCurrView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftInput();
                return false;
            }
        });
;   }

    private void hideSoftInput()
    {
        if(mPhoneNumberText != null) {
            inputManager.hideSoftInputFromWindow(mUsernameText.getWindowToken(), 0);
        }

        if(mSignatureText != null) {
            inputManager.hideSoftInputFromWindow(mSignatureText.getWindowToken(), 0);
        }
    }

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
        final Server.UserProfile profile = ProfilerManager.getInstance().getUserProfiler();

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
        mBirthDateText.setText(profile.getBirthDate());
        mBirthDateText.setFocusable(true);
        mPhoneNumberText.setText(profile.getPhoneNum());
        mPhoneNumberText.setFocusable(true);
        mLocationText.setText(profile.getLocation());
        mLocationText.setFocusable(true);
        mSignatureText.setText(profile.getSignature());
        mSignatureText.setFocusable(true);

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

        // 验证绑定手机
        mPhoneNumberText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterPage
            }
        });

        // 修改出生日期
        mBirthDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int defaultYear = 1990, defaultMonth = 10, defaultDay = 24;
                String birthDateStr = profile.getBirthDate();
                if(birthDateStr != null && !birthDateStr.isEmpty()) {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        Date date = formatter.parse(birthDateStr);

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);

                        defaultYear = calendar.get(Calendar.YEAR);
                        defaultMonth = calendar.get(Calendar.MONTH);
                        defaultDay = calendar.get(Calendar.DAY_OF_MONTH);

                    } catch (ParseException e) {
                        Log.e(TAG, "Date format not match!");
                    }
                }

                DatePickerDialog dialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        SimpleDateFormat dataFormatter = new SimpleDateFormat("yyyy-MM-dd");
                        Calendar cal = Calendar.getInstance();
                        cal.set(year, month, dayOfMonth);
                        String dateStr = dataFormatter.format(cal.getTime());

                        mBirthDateText.setText(dateStr);
                        setHasProfileChange(true);
                    }
                }, defaultYear, defaultMonth, defaultDay);
                dialog.show();
            }
        });

        // 修改所在位置
        mLocationText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftInput();

                CityPickerView cityPicker = new CityPickerView.Builder(getContext())
                        .textSize(20).title("地址选择")
                        .backgroundPop(0xa0000000)
                        .titleBackgroundColor("#234Dfa")
                        .titleTextColor("#000000")
                        .backgroundPop(0xa0000000)
                        .confirTextColor("#000000")
                        .cancelTextColor("#000000")
                        .province("上海")
                        .city("上海")
                        .district("浦东新区")
                        .textColor(Color.parseColor("#000000"))
                        .provinceCyclic(true)
                        .cityCyclic(false)
                        .districtCyclic(false)
                        .visibleItemsCount(7)
                        .itemPadding(10)
                        .onlyShowProvinceAndCity(false)
                        .build();
                cityPicker.show();

                cityPicker.setOnCityItemClickListener(new CityPickerView.OnCityItemClickListener() {
                    @Override
                    public void onSelected(ProvinceBean provinceBean, CityBean cityBean, DistrictBean districtBean) {
                        StringBuilder strBuilder = new StringBuilder();
                        strBuilder.append(provinceBean.getName());
                        strBuilder.append("-");
                        strBuilder.append(cityBean.getName());
                        strBuilder.append("-");
                        strBuilder.append(districtBean.getName());

                        String locationStr = strBuilder.toString();
                        mLocationText.setText(locationStr);
                        setHasProfileChange(true);
                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }
        });

        hasChanged = false;
    }


    private void setHasProfileChange(boolean flag)
    {
        hasChanged = flag;
        if(hasChanged) {
            setTopRightText(getString(R.string.save_profile_change));
        }else {
            hideTopLeftText();
        }
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

        uploadImageToServer(bitmap);
    }

    private void uploadImageToServer(Bitmap bitmap)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, stream);
        byte[] bytes = stream.toByteArray();
        String imgStr = new String(Base64.encodeToString(bytes, Base64.DEFAULT));

        String postStr = buildUploadAvatarStr(imgStr);

        AsyncHttpWorker httpWorker = new AsyncHttpWorker();
        httpWorker.post(AppConfig.UploadAvatarUrl, postStr, new AsyncHttpReplyHandler(){

            @Override
            public void onSuccess(String replyStr) {
                Toast.makeText(getContext(), getString(R.string.upload_avatar_success), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, String errorInfo) {

                String errorStr = getString(R.string.upload_avatar_failed) + " : " + errorInfo;

                Toast.makeText(getContext(), errorStr, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String buildUploadAvatarStr(String imgStr)
    {
        UploadRequest request = new UploadRequest();
        request.setUserId(ProfilerManager.getUserId());
        request.setType("avatar");
        request.setImage(imgStr);

        return JSON.toJSONString(request);
    }
}
