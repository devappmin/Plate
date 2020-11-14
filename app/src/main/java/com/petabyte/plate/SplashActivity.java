package com.petabyte.plate;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.petabyte.plate.ui.activity.MainActivity;

public class SplashActivity extends AppCompatActivity {

    private Intent intent;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null)
            intent = new Intent(SplashActivity.this, MainActivity.class);
        else
            intent = new Intent(SplashActivity.this, MainActivity.class); // MainActivity를 LoginActivity로 변경할 것

        startActivity(intent);
        finish();
    }
}