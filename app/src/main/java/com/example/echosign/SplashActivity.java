package com.example.echosign;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.example.echosign.utils.SessionManager;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 1500;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Initialize SessionManager
        sessionManager = new SessionManager(this);

        // Log current login status (for debugging)
        logLoginStatus();

        // Step 6.5: Keep navigation simple - always go to LoginActivity
        // We're NOT checking login status yet (that's for Step 7)
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Always navigate to LoginActivity for Step 6
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Close splash activity
            }
        }, SPLASH_DELAY);
    }

    /**
     * Log current login status for debugging
     */
    private void logLoginStatus() {
        boolean isLoggedIn = sessionManager.isLoggedIn();
        String username = sessionManager.getUsername();

        System.out.println("SplashActivity - Login Status Check:");
        System.out.println("Is logged in: " + isLoggedIn);
        System.out.println("Username: " + (username != null ? username : "null"));

        // Show what's stored in SharedPreferences
        String storedData = sessionManager.getAllData();
        System.out.println("Stored data: " + storedData);
    }
}