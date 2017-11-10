package com.rosinante24.firebaseauthentication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.facebook_login_button)
    Button facebookLoginButton;
    @BindView(R.id.google_login_button)
    Button googleLoginButton;
    @BindView(R.id.email_login_button)
    Button emailLoginButton;
    @BindView(R.id.anonymous_login_button)
    Button anonymousLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.facebook_login_button, R.id.google_login_button, R.id.email_login_button, R.id.anonymous_login_button})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.facebook_login_button:
                startActivity(new Intent(MainActivity.this, FacebookLoginActivity.class));
                break;
            case R.id.google_login_button:
                startActivity(new Intent(MainActivity.this, GoogleLoginActivity.class));
                break;
            case R.id.email_login_button:
                startActivity(new Intent(MainActivity.this, EmailLoginActivity.class));
                break;
            case R.id.anonymous_login_button:
                startActivity(new Intent(MainActivity.this, AnonymousLoginActivity.class));
                break;
        }
    }
}
