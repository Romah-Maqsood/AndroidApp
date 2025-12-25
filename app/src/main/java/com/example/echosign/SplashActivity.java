package com.example.echosign;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    // Splash screen duration (2 seconds)
    private static final int SPLASH_DELAY = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Handler to delay navigation
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // For now, always go to LoginActivity
                // In Step 4, we'll add login check here
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Close splash so user can't go back
            }
        }, SPLASH_DELAY);
    }
}