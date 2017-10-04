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

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.edit_text_email_register)
    EditText editTextEmailRegister;
    @BindView(R.id.edit_text_password_register)
    EditText editTextPasswordRegister;
    @BindView(R.id.edit_text_confirm_password_register)
    EditText editTextConfirmPasswordRegister;
    @BindView(R.id.button_register)
    Button buttonRegister;
    @BindView(R.id.text_link_to_forgot_password_register)
    TextView textLinkToForgotPasswordRegister;
    @BindView(R.id.text_link_to_register_login)
    TextView textLinkToRegisterLogin;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        firebaseAuth = FirebaseAuth.getInstance();

    }

    @OnClick({R.id.button_register, R.id.text_link_to_forgot_password_register, R.id.text_link_to_register_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_register:

                String emailregister = editTextEmailRegister.getText().toString();
                String passwordregister = editTextPasswordRegister.getText().toString();
                String confirmpasswordregister = editTextConfirmPasswordRegister.getText().toString();

                if (emailregister.isEmpty()) {
                    editTextEmailRegister.setError("Please enter your email address!");
                    editTextEmailRegister.requestFocus();
                } else if (passwordregister.isEmpty()) {
                    editTextPasswordRegister.setError("Please enter your password!");
                    editTextPasswordRegister.requestFocus();
                } else if (confirmpasswordregister.isEmpty()) {
                    editTextConfirmPasswordRegister.setError("Please confirm your password!");
                    editTextConfirmPasswordRegister.requestFocus();
                } else if (!confirmpasswordregister.equals(passwordregister)) {
                    editTextConfirmPasswordRegister.setError("Please enter your password confirmation clearly!");
                    editTextConfirmPasswordRegister.requestFocus();
                } else {
                    if (!emailregister.isEmpty() && !passwordregister.isEmpty() &&
                            !confirmpasswordregister.isEmpty() && confirmpasswordregister.equals(passwordregister)) {

                        progressBar.setVisibility(View.VISIBLE);
                        firebaseAuth.createUserWithEmailAndPassword(emailregister, passwordregister)
                                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        progressBar.setVisibility(View.GONE);

                                        if (!task.isSuccessful()) {
                                            Toast.makeText(RegisterActivity.this, "Authentication failed : " + task.getException(), Toast.LENGTH_SHORT).show();
                                        } else {

                                            finish();

                                        }
                                    }
                                });

                    }
                }

                break;
            case R.id.text_link_to_forgot_password_register:
                startActivity(new Intent(RegisterActivity.this, ForgotPasswordActivity.class));
                break;
            case R.id.text_link_to_register_login:
                finish();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}
