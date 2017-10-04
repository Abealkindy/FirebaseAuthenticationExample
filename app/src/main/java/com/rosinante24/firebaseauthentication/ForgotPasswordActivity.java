package com.rosinante24.firebaseauthentication;

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
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ForgotPasswordActivity extends AppCompatActivity {

    @BindView(R.id.edit_text_email_forgot)
    EditText editTextEmailForgot;
    @BindView(R.id.button_reset_password_forgot)
    Button buttonResetPasswordForgot;
    @BindView(R.id.text_back_forgot)
    TextView textBackForgot;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.bind(this);
        firebaseAuth = FirebaseAuth.getInstance();

    }

    @OnClick({R.id.button_reset_password_forgot, R.id.text_back_forgot})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_reset_password_forgot:
                String emailforgot = editTextEmailForgot.getText().toString();

                if (emailforgot.isEmpty()){
                    editTextEmailForgot.setError("Please enter your registered email confirmation!");
                    editTextEmailForgot.requestFocus();
                } else {

                    if (!emailforgot.isEmpty()){
                        progressBar.setVisibility(View.VISIBLE);
                        firebaseAuth.sendPasswordResetEmail(emailforgot)
                                .addOnCompleteListener(ForgotPasswordActivity.this, new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressBar.setVisibility(View.GONE);

                                        if (task.isSuccessful()){
                                            Toast.makeText(ForgotPasswordActivity.this, "We've sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(ForgotPasswordActivity.this, "Failed send reset email!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                }
                break;
            case R.id.text_back_forgot:
                finish();
                break;
        }
    }
}
