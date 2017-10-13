package com.rosinante24.firebaseauthentication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.edit_text_email_login)
    EditText editTextEmailLogin;
    @BindView(R.id.edit_text_password)
    EditText editTextPassword;
    @BindView(R.id.button_sign_in)
    Button buttonSignIn;
    @BindView(R.id.text_link_to_forgot_password_login)
    TextView textLinkToForgotPasswordLogin;
    @BindView(R.id.text_link_to_register)
    TextView textLinkToRegister;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.facebook_login_button)
    LoginButton facebookLoginButton;

    private FirebaseAuth firebaseAuth;
    private CallbackManager callbackManager;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
            finish();
        }

        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        FacebookSdk.sdkInitialize(getApplicationContext());
//        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();

        if (getIntent().hasExtra("logout")) {
            LoginManager.getInstance().logOut();
        }

        facebookLoginButton.setReadPermissions("email", "public_profile");
        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(LoginActivity.this, "Gagal om!", Toast.LENGTH_SHORT).show();
            }
        });

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    startActivity(new Intent(LoginActivity.this, FacebookProfileactivity.class));
                    finish();
                }

            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    private void handleFacebookAccessToken(AccessToken accessToken) {
        AuthCredential cretdential = FacebookAuthProvider.getCredential(accessToken.getToken());
        firebaseAuth.signInWithCredential(cretdential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Gk bisa Masuk!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @OnClick({R.id.button_sign_in, R.id.text_link_to_forgot_password_login, R.id.text_link_to_register})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_sign_in:

                firebaseAuth = FirebaseAuth.getInstance();

                String emaillogin = editTextEmailLogin.getText().toString();
                final String passwordlogin = editTextPassword.getText().toString();

                if (emaillogin.isEmpty()) {
                    editTextEmailLogin.setError("Please enter your email address!");
                    editTextEmailLogin.requestFocus();
                } else if (passwordlogin.isEmpty()) {
                    editTextPassword.setError("Please enter your password!");
                    editTextPassword.requestFocus();
                } else {
                    if (!emaillogin.isEmpty() && !passwordlogin.isEmpty()) {
                        progressBar.setVisibility(View.VISIBLE);
                        firebaseAuth.signInWithEmailAndPassword(emaillogin, passwordlogin)
                                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        progressBar.setVisibility(View.GONE);

                                        if (!task.isSuccessful()) {
                                            if (passwordlogin.length() < 6) {
                                                editTextPassword.setError("Password too short, enter minimum 6 characters!");
                                            } else {
                                                Toast.makeText(LoginActivity.this, "Authentication failed, check your email and password or sign up!", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
                                            finish();
                                        }

                                    }
                                });
                    }
                }
                break;
            case R.id.text_link_to_forgot_password_login:
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
                break;
            case R.id.text_link_to_register:
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
