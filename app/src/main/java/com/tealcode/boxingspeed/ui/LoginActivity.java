package com.tealcode.boxingspeed.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tealcode.boxingspeed.R;
import com.tealcode.boxingspeed.message.LoginEvent;
import com.tealcode.boxingspeed.message.SocketEvent;
import com.tealcode.boxingspeed.handler.ILoginReplyHandler;
import com.tealcode.boxingspeed.helper.AppConstant;
import com.tealcode.boxingspeed.helper.LocalStorage;
import com.tealcode.boxingspeed.manager.LoginManager;

public class LoginActivity extends Activity implements ILoginReplyHandler {

    private static final String TAG = "LoginActivity";
    private Handler loginHandler = new Handler();
    private View splashPage;
    private View loginPage;
    private View statusPage;
    private Button loginButton;

    private EditText mUsernameText;
    private EditText mPasswordText;
    private InputMethodManager inputManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inputManager = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        setContentView(R.layout.activity_login);

        LoginManager.getInstance().registerHandler(this);

        initViewLayout();

        // 显示欢迎界面
        showSplashPage();

        // 尝试自动登录
        startAutoLogin();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        splashPage = null;
        loginPage = null;
        statusPage = null;
    }

    // 初始化控件位置和引用
    private void initViewLayout()
    {
        splashPage = findViewById(R.id.splash_page);
        loginPage = findViewById(R.id.login_page);
        statusPage = findViewById(R.id.login_status);


        mUsernameText = (EditText)findViewById(R.id.username);
        mPasswordText = (EditText)findViewById(R.id.password);
        mPasswordText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == R.id.login || actionId == EditorInfo.IME_NULL) {
                    attemptLogin(true);
                }
                return false;
            }
        });

        loginButton = (Button)findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftInput();
                attemptLogin(true);
            }
        });

        loginPage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftInput();
                return false;
            }
        });
    }

    private void hideSoftInput()
    {
        if(mUsernameText != null) {
            inputManager.hideSoftInputFromWindow(mUsernameText.getWindowToken(), 0);
        }

        if(mPasswordText != null) {
            inputManager.hideSoftInputFromWindow(mPasswordText.getWindowToken(), 0);
        }
    }

    private void showSplashPage()
    {
        splashPage.setVisibility(View.VISIBLE);
        loginPage.setVisibility(View.INVISIBLE);

        Animation splashAnim = AnimationUtils.loadAnimation(this, R.anim.login_splash);
        if(splashAnim != null) {
            splashPage.startAnimation(splashAnim);
        }
        else {
            Log.e(TAG, "Login Splash Animation not found!");
        }

    }

    private void showLoginPage() {
        splashPage.setVisibility(View.GONE);
        loginPage.setVisibility(View.VISIBLE);
    }

    private void startAutoLogin()
    {
        // 用户主动退出时，不再执行自动登录，仅显示登录界面
        boolean canAutoLogin = shouldAutoLogin();
        if(!canAutoLogin) {
            showLoginPage();
            return;
        }

        boolean canTryAutoLogin = true;
        // 使用本地数据库中保存的用户名和密码尝试登录。
        String savedUsername = LocalStorage.getInstance().getStringValue(AppConstant.KEY_SAVED_USERNAME, "");
        String savedPassword = LocalStorage.getInstance().getStringValue(AppConstant.KEY_SAVED_PASSWORD, "");
        if(savedUsername == null || savedUsername.isEmpty()) {
            canTryAutoLogin = false;
        }

        if(savedPassword == null || savedPassword.isEmpty()) {
            canTryAutoLogin = false;
        }

        if(canTryAutoLogin) {
            LoginManager.getInstance().login(savedUsername, savedPassword);
        }else {
            // 延迟两秒，跳转到登录界面
            loginHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showLoginPage();
                }
            }, 1600);
        }
    }


    // 是否自动登录。每次开启都默认尝试自动登陆，除非用户主动最初返回到登录界面
    private boolean shouldAutoLogin()
    {
        Intent intent = getIntent();
        if(intent == null) {
            return true;
        }

        boolean autoLogin = intent.getBooleanExtra(AppConstant.KEY_AUTO_LOGIN, true);
        return autoLogin;
    }

    private void attemptLogin(boolean doHash)
    {
        String username = mUsernameText.getText().toString();
        String password = mPasswordText.getText().toString();
        boolean cancel = false;
        View focusView = null;

        if(TextUtils.isEmpty(username)) {
            Toast.makeText(this, getString(R.string.error_name_required), Toast.LENGTH_SHORT).show();
            focusView = mUsernameText;
            cancel = true;
        }

        if(TextUtils.isEmpty(password)) {
            Toast.makeText(this, getString(R.string.error_pwd_required), Toast.LENGTH_SHORT).show();
            focusView = mPasswordText;
            cancel = true;
        }

        if(cancel) {
            focusView.requestFocus();
            return;
        }

        showProgress(true);
        username = username.trim();
        LoginManager.getInstance().login(username, password);
    }

    private void showProgress(final boolean show) {
        if (show) {
            statusPage.setVisibility(View.VISIBLE);
        } else {
            statusPage.setVisibility(View.GONE);
        }
    }

    // 登录成功，切换到应用主页面
    private void onLoginSuccess()
    {

    }

    private void onLoginFailed(LoginEvent event)
    {
        switch(event) {
            case LOGIN_AUTH_FAILED:
                Toast.makeText(this, getString(R.string.error_incorrect_user), Toast.LENGTH_SHORT).show();
                break;
            case LOGIN_INNER_ERROR:
                Toast.makeText(this, getString(R.string.internal_error), Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
                break;
        }

    }

    // Implement ILoginReplyHandler callback functions
    @Override
    public void onLoginReply(LoginEvent event) {
        switch(event)
        {
            case LOGIN_SUCCESS:
                onLoginSuccess();
                break;
            case LOGIN_AUTH_FAILED:
            case LOGIN_INNER_ERROR:
                onLoginFailed(event);
                break;
            default:
                Log.e(TAG, "Invalid LoginReply Status");
        }
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
