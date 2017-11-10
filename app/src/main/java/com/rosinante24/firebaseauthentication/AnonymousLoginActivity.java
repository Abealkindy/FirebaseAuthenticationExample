package com.rosinante24.firebaseauthentication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AnonymousLoginActivity extends BaseActivity {

    @BindView(R.id.button_signin_anonymous)
    Button buttonSigninAnonymous;
    @BindView(R.id.button_signout_anonymous)
    Button buttonSignoutAnonymous;
    @BindView(R.id.editext_email_anonymous)
    EditText editextEmailAnonymous;
    @BindView(R.id.editext_password_anonymous)
    EditText editextPasswordAnonymous;
    @BindView(R.id.button_linkaccount)
    Button buttonLinkaccount;
    @BindView(R.id.text_profile)
    TextView textProfile;

    String emailAnonymous, passwordAnonymous;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anonymous_login);
        ButterKnife.bind(this);

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    Toast.makeText(AnonymousLoginActivity.this, "Masuk : " + firebaseUser.getUid(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AnonymousLoginActivity.this, "Keluar", Toast.LENGTH_SHORT).show();
                }
                updateUi(firebaseUser);
            }
        };

    }

    private void updateUi(FirebaseUser firebaseUser) {
        boolean isSignIn = (firebaseUser != null);
        if (isSignIn) {
            textProfile.setText("Email : " + firebaseUser.getEmail());
            textProfile.append("\n");
            textProfile.append("Firebase ID : " + firebaseUser.getUid());

            buttonSigninAnonymous.setEnabled(false);
            buttonSignoutAnonymous.setEnabled(true);
            buttonLinkaccount.setEnabled(true);
        } else {
            textProfile.setText(null);
            buttonSigninAnonymous.setEnabled(true);
            buttonSignoutAnonymous.setEnabled(false);
            buttonLinkaccount.setEnabled(false);
        }
        hideProgressDialog();
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    @OnClick({R.id.button_signin_anonymous, R.id.button_signout_anonymous, R.id.button_linkaccount})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_signin_anonymous:
                signInAnonymously();
                break;
            case R.id.button_signout_anonymous:
                signOut();
                break;
            case R.id.button_linkaccount:
                linkAccount();
                break;
        }
    }

    private void linkAccount() {
        if (!validateForm()) {
            return;
        }
        showProgressDialog();
        emailAnonymous = editextEmailAnonymous.getText().toString();
        passwordAnonymous = editextPasswordAnonymous.getText().toString();

        AuthCredential credential = EmailAuthProvider.getCredential(emailAnonymous, passwordAnonymous);
        firebaseAuth.getCurrentUser().linkWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    textProfile.setTextColor(Color.DKGRAY);
                    textProfile.setText(task.getException().getMessage());
                } else {
                    textProfile.setTextColor(Color.WHITE);
                }
                hideProgressDialog();
            }
        });
    }

    private boolean validateForm() {
        emailAnonymous = editextEmailAnonymous.getText().toString();
        passwordAnonymous = editextPasswordAnonymous.getText().toString();
        if (emailAnonymous.isEmpty()) {
            editextEmailAnonymous.setError("Please fill your email!");
            return false;
        } else if (passwordAnonymous.isEmpty()) {
            editextPasswordAnonymous.setError("Please fill your password!");
            return false;
        } else {
            editextEmailAnonymous.setError(null);
            editextPasswordAnonymous.setError(null);
            return true;
        }
    }

    private void signOut() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage("Seriuosly Logout?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        firebaseAuth.signOut();
                        updateUi(null);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    private void signInAnonymously() {
        showProgressDialog();
        firebaseAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    textProfile.setTextColor(Color.DKGRAY);
                    textProfile.setText(task.getException().getMessage());
                } else {
                    textProfile.setTextColor(Color.WHITE);
                }
                hideProgressDialog();
            }
        });
    }
}
