package com.rosinante24.firebaseauthentication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GoogleProfileActivity extends AppCompatActivity {

    @BindView(R.id.image_profile_google)
    ImageView imageProfileGoogle;
    @BindView(R.id.text_nama_google)
    TextView textNamaGoogle;
    @BindView(R.id.text_email_google)
    TextView textEmailGoogle;
    @BindView(R.id.button_logout_google)
    Button buttonLogoutGoogle;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_profile);
        ButterKnife.bind(this);

        firebaseAuth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    textNamaGoogle.setText(user.getDisplayName());
                    textEmailGoogle.setText(user.getEmail());
                    Glide.with(GoogleProfileActivity.this)
                            .load(user.getPhotoUrl())
                            .into(imageProfileGoogle);
                } else {
                    firebaseAuth.signOut();
                    startActivity(new Intent(GoogleProfileActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };
    }

    @OnClick(R.id.button_logout_google)
    public void onClick() {
        firebaseAuth.signOut();
    }
}
