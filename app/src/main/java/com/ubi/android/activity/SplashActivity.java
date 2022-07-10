package com.ubi.android.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.ubi.android.MainActivity;
import com.ubi.android.R;
import com.ubi.android.utils.AppPreferences;


public class SplashActivity extends AppCompatActivity {

    private final static int SPLASH_SCREEN = 2000;
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            boolean isLoggedIn =AppPreferences.getInstance().isUserLoggedIn(getApplicationContext());
            if (!isLoggedIn) {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            } else {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
//            startActivity(new Intent(SplashActivity.this, MainActivity.class));

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        handler.postDelayed(runnable, SPLASH_SCREEN);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null && runnable != null)
            handler.removeCallbacks(runnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && runnable != null)
            handler.removeCallbacks(runnable);
    }
}