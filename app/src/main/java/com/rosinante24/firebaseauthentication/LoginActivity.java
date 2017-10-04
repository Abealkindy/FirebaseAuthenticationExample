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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

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

    FirebaseAuth firebaseAuth;

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
}
