package com.tealcode.boxingspeed.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.tealcode.boxingspeed.R;
import com.tealcode.boxingspeed.helper.AppConstant;

public class LoginActivity extends BaseActivity {

    private Handler loginHandler = new Handler();
    private View splashPage;
    private View loginPage;
    private View statusPage;

    private EditText mUsernameText;
    private EditText mPasswordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        initViewLayout();

        // 显示欢迎界面
        showSplashPage();

        loginHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showLoginPage();
            }
        }, 2000);

    }

    // 初始化控件位置和引用
    private void initViewLayout()
    {
        splashPage = findViewById(R.id.splash_page);
        loginPage = findViewById(R.id.login_page);
        statusPage = findViewById(R.id.login_status);

        mUsernameText = (EditText)findViewById(R.id.username);
        mPasswordText = (EditText)findViewById(R.id.password);
    }

    private void showSplashPage()
    {
        splashPage.setVisibility(View.VISIBLE);
        loginPage.setVisibility(View.INVISIBLE);
    }

    private void showLoginPage() {
        splashPage.setVisibility(View.GONE);
        loginPage.setVisibility(View.VISIBLE);
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
}
