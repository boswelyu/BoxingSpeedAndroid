package com.tealcode.boxingspeed.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tealcode.boxingspeed.R;
import com.tealcode.boxingspeed.handler.IRegisterReplyHandler;
import com.tealcode.boxingspeed.helper.AppConstant;
import com.tealcode.boxingspeed.manager.LoginManager;
import com.tealcode.boxingspeed.message.RegisterRequest;
import com.tealcode.boxingspeed.utility.TextUtil;

import cn.smssdk.SMSSDK;

public class RegisterActivity extends Activity implements IRegisterReplyHandler {

    private static final String TAG = "RegisterActivity";

    // Email Address register used components
    private EditText mUsernameText;
    private EditText mPasswordText;
    private EditText mConfirmPasswordText;
    private Button   mEmailRegisterButton;

    // Phone Number Login used components
    private EditText mPhoneNumberText;
    private EditText mPhonePassCodeText;
    private Button   mSendPassCodeButton;
    private Button   mPhoneRegisterButton;

    private InputMethodManager inputManager;

    private View mEmailRegisterPage;
    private View mPhoneNumLoginPage;
    private View mStatusPage;

    private boolean mIsEmailRegister = false;
    private boolean mIsPhoneRegister = false;

    private Thread timerThread = null;
    private final int CODE_TIMING = 1;     // 正在倒计时
    private final int CODE_COOLDOWN = 2;   // 倒计时结束
    private MessageHandler mMessageHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inputManager = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        setContentView(R.layout.activity_register);

        LoginManager.getInstance().setRegisterHandler(this);

        initViewLayout();

        initListeners();

        // 根据传入参数决定显示手机注册还是邮箱注册
        displayView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        // 按返回键之后，返回到登录界面
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        intent.putExtra(AppConstant.KEY_AUTO_LOGIN, false);
        startActivity(intent);

        this.finish();
    }

    // 初始化控件位置和引用
    private void initViewLayout()
    {
        mEmailRegisterPage = findViewById(R.id.email_register_page);
        mEmailRegisterPage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftInput();
                return false;
            }
        });
        mPhoneNumLoginPage = findViewById(R.id.phone_number_register_page);
        mPhoneNumLoginPage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftInput();
                return false;
            }
        });

        mStatusPage = findViewById(R.id.register_status_page);

        mUsernameText = (EditText) findViewById(R.id.email_username);
        mPasswordText = (EditText) findViewById(R.id.email_password);
        mConfirmPasswordText = (EditText) findViewById(R.id.confirm_password);
        mEmailRegisterButton = (Button) findViewById(R.id.email_register_button);

        mPhoneNumberText = (EditText) findViewById(R.id.phone_number_text);
        mSendPassCodeButton = (Button) findViewById(R.id.send_register_code);
        mPhonePassCodeText = (EditText) findViewById(R.id.phone_pass_code_text);
        mPhoneRegisterButton = (Button) findViewById(R.id.phone_register_button);

    }

    private void initListeners()
    {
        mSendPassCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPassCode();
            }
        });

        mEmailRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptEmailRegister();
            }
        });

        mPhoneRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptPhoneRegister();
            }
        });
    }

    private void displayView()
    {
        Intent intent = getIntent();
        if(intent == null) {
            showEmailRegisterPage();
            return;
        }

        String regType = intent.getStringExtra(AppConstant.KEY_REGISTER_TYPE);
        if(regType == null || regType.equals(AppConstant.VALUE_EMAIL_REG)) {
            // 默认显示邮箱注册界面
            Log.d(TAG, "Show Email Register Page");
            showEmailRegisterPage();
        }
        else {
            Log.d(TAG, "Show Phone Number Register Page");
            showPhoneRegisterPage();
        }

        showProgress(false);
    }

    private void hideSoftInput()
    {
        if(mUsernameText != null) {
            inputManager.hideSoftInputFromWindow(mUsernameText.getWindowToken(), 0);
        }

        if(mPasswordText != null) {
            inputManager.hideSoftInputFromWindow(mPasswordText.getWindowToken(), 0);
        }

        if(mConfirmPasswordText != null) {
            inputManager.hideSoftInputFromWindow(mPasswordText.getWindowToken(), 0);
        }

        if(mPhoneNumberText != null) {
            inputManager.hideSoftInputFromWindow(mPhoneNumberText.getWindowToken(), 0);
        }

        if(mPhonePassCodeText != null) {
            inputManager.hideSoftInputFromWindow(mPhonePassCodeText.getWindowToken(), 0);
        }
    }

    private void showEmailRegisterPage()
    {
        mEmailRegisterPage.setVisibility(View.VISIBLE);
        mPhoneNumLoginPage.setVisibility(View.INVISIBLE);

        mIsEmailRegister = true;
        mIsPhoneRegister = false;
    }

    private void showPhoneRegisterPage() {
        mEmailRegisterPage.setVisibility(View.INVISIBLE);
        mPhoneNumLoginPage.setVisibility(View.VISIBLE);

        mIsEmailRegister = false;
        mIsPhoneRegister = true;

        if(mMessageHandler == null) {
            mMessageHandler = new MessageHandler();
        }
    }

    private void showProgress(final boolean show) {
        if (show) {
            mStatusPage.setVisibility(View.VISIBLE);
        } else {
            mStatusPage.setVisibility(View.GONE);
        }
    }

    private void sendPassCode()
    {
        String phonenum = mPhoneNumberText.getText().toString();

        if(phonenum.isEmpty() || !TextUtil.isValidPhoneNumber(phonenum)) {
            Toast.makeText(this, getString(R.string.invalid_phonenum), Toast.LENGTH_LONG).show();
            mPhoneNumberText.requestFocus();
            return;
        }

        SMSSDK.getVerificationCode("86", phonenum);

        // 60秒内不允许再次发送验证码
        mSendPassCodeButton.setClickable(false);
        if(timerThread != null) {
            timerThread.interrupt();
        }

        timerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i = 60; i > 0; i--) {
                    Message msg = new Message();
                    msg.arg1 = i;
                    msg.what = CODE_TIMING;
                    mMessageHandler.sendMessage(msg);
                    try {
                        Thread.sleep(1000);
                    }catch (Exception e) {
                        Log.e(TAG, "Timer thread interrupted by exception: " + e.toString());
                    }
                }

                mMessageHandler.sendEmptyMessage(CODE_COOLDOWN);
            }
        });
        timerThread.start();
    }

    private void attemptEmailRegister()
    {
        View focusView = null;
        boolean cancel = false;

        if(mIsEmailRegister == false) {
            Toast.makeText(this, "Internal Error: Not Email Register Page Show", Toast.LENGTH_SHORT).show();
            return;
        }

        String regUserName = mUsernameText.getText().toString();
        String regPassword = mPasswordText.getText().toString();
        String regConfirmPassword = mConfirmPasswordText.getText().toString();

        if(TextUtils.isEmpty(regUserName)) {
            Toast.makeText(this, getString(R.string.error_name_required), Toast.LENGTH_SHORT).show();
            focusView = mUsernameText;
            cancel = true;
        }

        if(TextUtils.isEmpty(regPassword)) {
            Toast.makeText(this, getString(R.string.error_pwd_required), Toast.LENGTH_SHORT).show();
            focusView = mPasswordText;
            cancel = true;
        }

        if(TextUtils.isEmpty(regConfirmPassword)) {
            Toast.makeText(this, getString(R.string.error_confirm_pwd_required), Toast.LENGTH_SHORT).show();
            focusView = mConfirmPasswordText;
            cancel = true;
        }

        if(!TextUtils.equals(regPassword, regConfirmPassword)) {
            Toast.makeText(this, getString(R.string.error_confirm_pwd_mismatch), Toast.LENGTH_SHORT).show();
            focusView = mConfirmPasswordText;
            cancel = true;
        }

        if(cancel) {
            focusView.requestFocus();
            return;
        }

        showProgress(true);

        regUserName = regUserName.trim();
        LoginManager.getInstance().register(regUserName, regPassword, RegisterRequest.EMAIL_REG);
    }

    private void attemptPhoneRegister()
    {
        if(mIsPhoneRegister == false)
        {
            Toast.makeText(this, "Internal Error: Not Phone Register Page Show", Toast.LENGTH_SHORT).show();
            return;
        }

        String phoneNumStr = mPhoneNumberText.getText().toString().trim();
        String passCodeStr = mPhonePassCodeText.getText().toString().trim();
        if(TextUtils.isEmpty(phoneNumStr)) {
            Toast.makeText(this, getString(R.string.invalid_phonenum), Toast.LENGTH_SHORT).show();
            mPhoneNumberText.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(passCodeStr)) {
            Toast.makeText(this, getString(R.string.empty_pass_code), Toast.LENGTH_SHORT).show();
            mPasswordText.requestFocus();
            return;
        }

        showProgress(true);

        LoginManager.getInstance().register(phoneNumStr, passCodeStr, RegisterRequest.PHONE_REG);
    }


    // Implement IRegisterReplyHandler callback functions
    @Override
    public void onRegisterSuccess() {

        showProgress(false);

        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.putExtra(AppConstant.KEY_SET_PROFILE, true);
        startActivity(intent);

        RegisterActivity.this.finish();
    }

    @Override
    public void onRegisterFailure(int retcode, String errorInfo)
    {
        Toast.makeText(this, errorInfo, Toast.LENGTH_SHORT).show();

        hideSoftInput();
        showProgress(false);

        if(retcode == -6) {
            // Duplex Username
            mUsernameText.requestFocus();
            return;
        }
    }

    // 处理倒计时消息的内部类
    private class MessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case CODE_TIMING:
                    int rt = msg.arg1;
                    mSendPassCodeButton.setText(getString(R.string.resend_pass_code) + "(" + rt + "s)");
                    break;
                case CODE_COOLDOWN:
                    mSendPassCodeButton.setText(getString(R.string.send_register_code));
                    mSendPassCodeButton.setClickable(true);
                    break;
            }
        }
    }

}
