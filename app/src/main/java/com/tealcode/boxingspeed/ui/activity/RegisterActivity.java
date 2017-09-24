package com.tealcode.boxingspeed.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.tealcode.boxingspeed.R;
import com.tealcode.boxingspeed.handler.IRegisterReplyHandler;
import com.tealcode.boxingspeed.helper.AppConstant;
import com.tealcode.boxingspeed.manager.LoginManager;
import com.tealcode.boxingspeed.message.RegisterReply;
import com.tealcode.boxingspeed.message.SocketEvent;

public class RegisterActivity extends Activity implements IRegisterReplyHandler {

    private static final String TAG = "RegisterActivity";

    // Email Address register used components
    private EditText mUsernameText;
    private EditText mPasswordText;
    private EditText mConfirmPasswordText;

    // Phone Number Login used components
    private EditText mPhoneNumberText;
    private EditText mPhonePassCodeText;
    private Button   mSendPassCodeButton;

    private InputMethodManager inputManager;

    private View mEmailRegisterPage;
    private View mPhoneNumLoginPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inputManager = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        setContentView(R.layout.activity_register);

        LoginManager.getInstance().setRegisterHandler(this);

        initViewLayout();

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
    }

    // 初始化控件位置和引用
    private void initViewLayout()
    {
        mEmailRegisterPage = findViewById(R.id.email_register_page);
        mPhoneNumLoginPage = findViewById(R.id.phone_number_register_page);
    }

    private void displayView()
    {
        Intent intent = getIntent();
        if(intent == null || intent.getStringExtra(AppConstant.KEY_REGISTER_TYPE).equals(AppConstant.VALUE_EMAIL_REG)) {
            // 默认显示邮箱注册界面
            showEmailRegisterPage();
        }
        else {
            showPhoneRegisterPage();
        }
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
    }

    private void showEmailRegisterPage()
    {
        mEmailRegisterPage.setVisibility(View.VISIBLE);
        mPhoneNumLoginPage.setVisibility(View.INVISIBLE);
    }

    private void showPhoneRegisterPage() {
        mEmailRegisterPage.setVisibility(View.INVISIBLE);
        mPhoneNumLoginPage.setVisibility(View.VISIBLE);
    }

    private void attemptRegister()
    {

    }



    // Implement IRegisterReplyHandler callback functions
    @Override
    public void onRegisterReply(RegisterReply reply) {

    }

    @Override
    public void onSocketEvent(SocketEvent event) {
        switch(event) {
            case NONE:
                onSocketEvent(event);
                break;
            default:
                Log.e(TAG, "Unknown socket event");
        }
    }
}
