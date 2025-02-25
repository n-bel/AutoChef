package com.g04.autochefmobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.g04.autochefmobile.R;

/**
 * Activity that will display a loading page when launching the app
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();

        final Intent intent = new Intent(SplashActivity.this, ShoppingListActivity.class);
        new Handler().postDelayed(() -> {
            startActivity(intent);
            finish();
        }, 2000);
    }

    @Override
    protected void onStop () {
        super.onStop();
    }
}