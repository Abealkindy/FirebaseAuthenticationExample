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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.io.InputStream;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GoogleLoginActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener {

    @BindView(R.id.logo)
    ImageView logo;
    @BindView(R.id.text_name_profile)
    TextView textNameProfile;
    @BindView(R.id.sign_out_button)
    Button signOutButton;
    @BindView(R.id.disconnect_button)
    Button disconnectButton;
    @BindView(R.id.sign_out_and_disconnect)
    LinearLayout signOutAndDisconnect;
    @BindView(R.id.google_sign_in_button)
    SignInButton googleSignInButton;

    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_login);
        ButterKnife.bind(this);
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions)
                .build();

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Toast.makeText(GoogleLoginActivity.this, "User ID : " + user.getUid(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(GoogleLoginActivity.this, "Keluar!", Toast.LENGTH_SHORT).show();
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
            textNameProfile.setText("Name : " + user.getDisplayName());
            textNameProfile.append("\n\n");
            textNameProfile.append("Email : " + user.getEmail());
            textNameProfile.append("\n\n");
            textNameProfile.append("Firebase ID : " + user.getUid());

            googleSignInButton.setVisibility(View.GONE);
            signOutAndDisconnect.setVisibility(View.VISIBLE);
        } else {
            logo.getLayoutParams().width = (getResources().getDisplayMetrics().widthPixels / 100) * 64;
            logo.setImageResource(R.drawable.firebaseicon);
            textNameProfile.setText(null);

            googleSignInButton.setVisibility(View.VISIBLE);
            signOutAndDisconnect.setVisibility(View.GONE);
        }
        hideProgressDialog();
    }

    @OnClick({R.id.sign_out_button, R.id.disconnect_button, R.id.google_sign_in_button})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_out_button:
                signOut();
                break;
            case R.id.disconnect_button:
                revokeAccess();
                break;
            case R.id.google_sign_in_button:
                signIn();
                break;
        }
    }

    private void revokeAccess() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage("Seriously Logout?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        firebaseAuth.signOut();
                        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                                new ResultCallback<Status>() {
                                    @Override
                                    public void onResult(@NonNull Status status) {
                                        updateUI(null);
                                    }
                                }
                        );
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

    private void signIn() {
        Intent googleSignInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(googleSignInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage("Seriously Logout?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        firebaseAuth.signOut();
                        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                                new ResultCallback<Status>() {
                                    @Override
                                    public void onResult(@NonNull Status status) {
                                        updateUI(null);
                                    }
                                }
                        );
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
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                updateUI(null);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Toast.makeText(this, "Auth Google : " + account.getId(), Toast.LENGTH_SHORT).show();
        showProgressDialog();
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Toast.makeText(GoogleLoginActivity.this, "Result : " + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                if (!task.isSuccessful()) {
                    textNameProfile.setTextColor(Color.RED);
                    textNameProfile.setText(task.getException().getMessage());
                } else {
                    textNameProfile.setTextColor(Color.WHITE);
                }
                hideProgressDialog();
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Google Play Services Error!", Toast.LENGTH_SHORT).show();
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
