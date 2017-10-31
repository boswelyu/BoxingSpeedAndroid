package com.tealcode.boxingspeed.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tealcode.boxingspeed.R;
import com.tealcode.boxingspeed.handler.PhoneVerifyHandler;
import com.tealcode.boxingspeed.manager.MessagePublisher;
import com.tealcode.boxingspeed.utility.TextUtil;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by YuBo on 2017/10/26.
 */

public class PhoneVerifyPage extends Activity {

    private static final String TAG = "PhoneVerifyPage";

    private static PhoneVerifyHandler mEventHandler = null;

    private static final int CODE_TIMING = 1;
    private static final int CODE_COOLDOWN = 2;
    private static final int CODE_SMSSDK_MSG = 3;

    // Public method
    public static void registerEventHandler(PhoneVerifyHandler handler)
    {
        mEventHandler = handler;
    }

    public static void show(Context context)
    {
        Intent intent = new Intent(context, PhoneVerifyPage.class);
        context.startActivity(intent);
    }

    // View functionality

    private EditText mPhoneNumberText;
    private Button   mSendPassCodeBtn;
    private EditText mPassCodeText;
    private Button   mBindPhoneBtn;
    private String   mInputPhoneNum;

    private Handler  mMessageHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initLayout();
        initListeners();

        initSDK();
    }

    private void initLayout()
    {
        setContentView(R.layout.activity_phone_verify);

        mPhoneNumberText = (EditText) findViewById(R.id.phone_number_text);
        mSendPassCodeBtn = (Button) findViewById(R.id.send_pass_code);
        mPassCodeText = (EditText) findViewById(R.id.pass_code_text);
        mBindPhoneBtn = (Button) findViewById(R.id.phone_bind_btn);
    }

    private void initListeners()
    {
        mMessageHandler = new MessageHandler(this);

        mSendPassCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPassCode();
            }
        });

        mBindPhoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyPassCode();
            }
        });
    }

    // 初始化短信功能SDK
    private void initSDK()
    {
        EventHandler eventHandler = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                msg.what = CODE_SMSSDK_MSG;
                mMessageHandler.sendMessage(msg);
            }
        };

        SMSSDK.registerEventHandler(eventHandler);
    }

    // 检查输入手机号的合法性，然后发送验证码
    private void sendPassCode()
    {
        String phonenum = mPhoneNumberText.getText().toString();

        if(phonenum.isEmpty() || !TextUtil.isValidPhoneNumber(phonenum)) {
            Toast.makeText(this, getString(R.string.invalid_phonenum), Toast.LENGTH_LONG).show();
            mPhoneNumberText.requestFocus();
            return;
        }

        mInputPhoneNum = phonenum;
        SMSSDK.getVerificationCode("86", phonenum);

        // 60秒内不允许再次发送验证码
        mSendPassCodeBtn.setClickable(false);
        startTimerThread(60);
    }

    private void verifyPassCode()
    {
        String passCode = mPassCodeText.getText().toString();
        if(passCode.isEmpty()) {
            Toast.makeText(this, getString(R.string.invalid_passcode), Toast.LENGTH_LONG).show();
            mPassCodeText.requestFocus();
            return;
        }

        SMSSDK.submitVerificationCode("86", mInputPhoneNum, passCode);
    }

    private Thread timerThread = null;
    private void startTimerThread(final int seconds) {
        if(timerThread != null) {
            timerThread.interrupt();
        }

        timerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i = seconds; i > 0; i--) {
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

    private class MessageHandler extends Handler {

        private Activity parentActivity = null;

        public MessageHandler(Activity parentAct) {
            this.parentActivity = parentAct;
        }

        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case CODE_TIMING:
                    int sec = msg.arg1;
                    mSendPassCodeBtn.setText(getString(R.string.resend_pass_code) + "(" + sec + "s)");
                    break;
                case CODE_COOLDOWN:
                    // 可以重新发送了
                    mSendPassCodeBtn.setText(getString(R.string.send_register_code));
                    mSendPassCodeBtn.setClickable(true);
                    break;
                case CODE_SMSSDK_MSG:
                    handleSMSResult(msg);
            }
        }

        private void handleSMSResult(Message msg) {

            int event = msg.arg1;
            int result = msg.arg2;
            Object data = msg.obj;

            if(result == SMSSDK.RESULT_COMPLETE) {
                switch(event) {
                    case SMSSDK.EVENT_GET_VERIFICATION_CODE:
                        // 验证码发送成功
                        Toast.makeText(getApplicationContext(), getString(R.string.passcode_sentout), Toast.LENGTH_SHORT).show();
                        break;
                    case SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE:
                        // 验证成功
                        Toast.makeText(getApplicationContext(), getString(R.string.passcode_verify_success), Toast.LENGTH_SHORT).show();
                        if(mEventHandler != null) {
                            mEventHandler.onSuccess(mInputPhoneNum);
                        }
                        // 绑定成功，退出当前验证界面
                        parentActivity.finish();
                        break;
                } // End of switch event
            } else if(result == SMSSDK.RESULT_ERROR) {
                // 验证失败，尝试从失败返回中获取错误描述信息
                Log.e(TAG, "Verify pass code failed");
                try{
                    Throwable throwable = (Throwable)data;
                    JSONObject object = JSON.parseObject(throwable.getMessage());
                    String desc = object.getString("detail");
                    if(desc == null || desc.isEmpty()) {
                        desc = getString(R.string.passcode_verify_failed);
                    }

                    if(mEventHandler != null) {
                        mEventHandler.onFailure(desc);
                    }

                }catch(Exception e) {
                    Log.e(TAG, "Verify pass code failed with exception: " + e.toString());
                    if(mEventHandler != null) {
                        mEventHandler.onFailure(getString(R.string.passcode_verify_failed) + e.toString());
                    }
                }
            }
        }
    }
}
