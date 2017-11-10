package com.rosinante24.firebaseauthentication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.InputStream;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EmailLoginActivity extends BaseActivity {

    @BindView(R.id.logo)
    ImageView logo;
    @BindView(R.id.name_profile_text)
    TextView nameProfileText;
    @BindView(R.id.edittext_email_emaillogin)
    EditText edittextEmailEmaillogin;
    @BindView(R.id.edittext_password_emaillogin)
    EditText edittextPasswordEmaillogin;
    @BindView(R.id.button_singin_maillogin)
    Button buttonSinginMaillogin;
    @BindView(R.id.button_createaccount_maillogin)
    Button buttonCreateaccountMaillogin;
    @BindView(R.id.verify_button)
    Button verifyButton;
    @BindView(R.id.sign_out_button)
    Button signOutButton;
    @BindView(R.id.signout_zone)
    LinearLayout signoutZone;
    @BindView(R.id.emailpassword_fields)
    LinearLayout emailpasswordFields;
    @BindView(R.id.button_login_zone)
    LinearLayout buttonLoginZone;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_login);
        ButterKnife.bind(this);
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Toast.makeText(EmailLoginActivity.this, "Masuk : " + user.getUid(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EmailLoginActivity.this, "Keluar!", Toast.LENGTH_SHORT).show();
                }
                updateUI(user);
            }
        };

    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            if (user.getPhotoUrl() != null) {
                new DownloadImageTask().execute(user.getPhotoUrl().toString());
            }
            nameProfileText.setText("Name : " + user.getDisplayName());
            nameProfileText.append("\n\n");
            nameProfileText.append("Email : " + user.getEmail());
            nameProfileText.append("\n\n");
            nameProfileText.append("Firebase ID : " + user.getUid());
            nameProfileText.append("\n\n");
            nameProfileText.append("Email Verification : " + user.isEmailVerified());
            if (user.isEmailVerified()) {
                verifyButton.setVisibility(View.GONE);
            } else {
                verifyButton.setVisibility(View.VISIBLE);
            }
            emailpasswordFields.setVisibility(View.GONE);
            buttonLoginZone.setVisibility(View.GONE);
            signoutZone.setVisibility(View.VISIBLE);
        } else {
            nameProfileText.setText(null);
            emailpasswordFields.setVisibility(View.VISIBLE);
            buttonLoginZone.setVisibility(View.VISIBLE);
            signoutZone.setVisibility(View.GONE);
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

    @OnClick({R.id.button_singin_maillogin, R.id.button_createaccount_maillogin, R.id.verify_button, R.id.sign_out_button})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_singin_maillogin:
                signIn(edittextEmailEmaillogin.getText().toString(), edittextPasswordEmaillogin.getText().toString());
                break;
            case R.id.button_createaccount_maillogin:
                createAccount(edittextEmailEmaillogin.getText().toString(), edittextPasswordEmaillogin.getText().toString());
                break;
            case R.id.verify_button:
                verifiyAccount();
                break;
            case R.id.sign_out_button:
                signOut();
                break;
        }
    }

    private void signOut() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage("Seriously Logout?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        firebaseAuth.signOut();
                        updateUI(null);
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

    private void verifiyAccount() {
        verifyButton.setEnabled(false);
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        user.sendEmailVerification().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(EmailLoginActivity.this, "Verification email has sent to : " + user.getEmail(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EmailLoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
                verifyButton.setEnabled(true);
            }
        });
    }

    private void signIn(String email, String password) {
        if (!validateForm()) {
            return;
        }
        showProgressDialog();
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    nameProfileText.setTextColor(Color.RED);
                    nameProfileText.setText(task.getException().getMessage());
                } else {
                    nameProfileText.setTextColor(Color.WHITE);
                }
                hideProgressDialog();
            }
        });
    }

    private void createAccount(String email, String password) {
        if (!validateForm()) {
            return;
        }
        showProgressDialog();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    nameProfileText.setTextColor(Color.RED);
                    nameProfileText.setText(task.getException().getMessage());
                } else {
                    nameProfileText.setTextColor(Color.WHITE);
                }
                hideProgressDialog();
            }
        });
    }

    private boolean validateForm() {
        if (edittextEmailEmaillogin.getText().toString().isEmpty()) {
            edittextEmailEmaillogin.setError("Please fill your email!");
            return false;
        } else if (edittextPasswordEmaillogin.getText().toString().isEmpty()) {
            edittextPasswordEmaillogin.setError("Please fill your password!");
            return false;
        } else {
            edittextEmailEmaillogin.setError(null);
            edittextPasswordEmaillogin.setError(null);
            return true;
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap mIcon = null;
            try {
                InputStream in = new URL(urls[0]).openStream();
                mIcon = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                logo.getLayoutParams().width = (getResources().getDisplayMetrics().widthPixels / 100) * 24;
                logo.setImageBitmap(result);
            }
        }
    }
}
