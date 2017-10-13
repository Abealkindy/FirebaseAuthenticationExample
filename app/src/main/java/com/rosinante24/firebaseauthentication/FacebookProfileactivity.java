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
import com.google.firebase.auth.UserInfo;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FacebookProfileactivity extends AppCompatActivity {

    @BindView(R.id.image_profile)
    ImageView imageProfile;
    @BindView(R.id.text_nama)
    TextView textNama;
    @BindView(R.id.text_email)
    TextView textEmail;
    @BindView(R.id.button_logout)
    Button buttonLogout;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_profileactivity);
        ButterKnife.bind(this);

        firebaseAuth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    textNama.setText(user.getDisplayName());
                    textEmail.setText(user.getEmail());
                    Glide.with(FacebookProfileactivity.this)
                            .load(user.getPhotoUrl())
                            .into(imageProfile);
                } else {
                    Intent intent = new Intent(FacebookProfileactivity.this, LoginActivity.class);
                    intent.putExtra("logout", true);
                    startActivity(intent);
                    finish();
                }
            }
        };

    }

    @OnClick(R.id.button_logout)
    public void onClick() {
        firebaseAuth.signOut();
    }
}
