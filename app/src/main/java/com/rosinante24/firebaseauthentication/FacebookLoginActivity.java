package com.rosinante24.firebaseauthentication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.InputStream;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FacebookLoginActivity extends BaseActivity {

    @BindView(R.id.text_profile_name)
    TextView textProfileName;
    @BindView(R.id.button_facebook_signout)
    Button buttonFacebookSignout;
    @BindView(R.id.button_facebook_login)
    LoginButton buttonFacebookLogin;
    @BindView(R.id.logo)
    ImageView logo;

    private CallbackManager callbackManager;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.activity_facebook_login);
        ButterKnife.bind(this);

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Toast.makeText(FacebookLoginActivity.this, "Masuk!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FacebookLoginActivity.this, "Kosong!", Toast.LENGTH_SHORT).show();
                }
                updateUI(user);
            }
        };
        callbackManager = CallbackManager.Factory.create();
        buttonFacebookLogin.setReadPermissions("email", "public_profile");
        buttonFacebookLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(FacebookLoginActivity.this, "Result : " + loginResult, Toast.LENGTH_SHORT).show();
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(FacebookLoginActivity.this, "Cancel!", Toast.LENGTH_SHORT).show();
                updateUI(null);
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(FacebookLoginActivity.this, "Error : " + error, Toast.LENGTH_SHORT).show();
                updateUI(null);
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken accessToken) {
        Toast.makeText(this, "Token : " + accessToken, Toast.LENGTH_SHORT).show();
        showProgressDialog();
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Toast.makeText(FacebookLoginActivity.this, "Result : " + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                if (!task.isSuccessful()) {
                    textProfileName.setTextColor(Color.RED);
                    textProfileName.setText(task.getException().getMessage());
                } else {
                    textProfileName.setTextColor(Color.WHITE);
                }
                hideProgressDialog();
            }
        });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            if (user.getPhotoUrl() != null) {
                new DownloadImageTask().execute(user.getPhotoUrl().toString());
            }
            textProfileName.setText("Name : " + user.getDisplayName());
            textProfileName.append("\n\n");
            textProfileName.append("Email : " + user.getEmail());
            textProfileName.append("\n\n");
            textProfileName.append("Firebase ID : " + user.getUid());

            buttonFacebookLogin.setVisibility(View.GONE);
            buttonFacebookSignout.setVisibility(View.VISIBLE);
        } else {
            logo.getLayoutParams().width = (getResources().getDisplayMetrics().widthPixels / 100) * 64;
            logo.setImageResource(R.drawable.firebaseicon);

            textProfileName.setText(null);

            buttonFacebookLogin.setVisibility(View.VISIBLE);
            buttonFacebookSignout.setVisibility(View.GONE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick({R.id.button_facebook_signout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_facebook_signout:
                signOut();
                break;
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
                        LoginManager.getInstance().logOut();
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
