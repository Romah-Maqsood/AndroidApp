package com.example.echosign;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.example.echosign.utils.SessionManager;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 1500; // 1.5 seconds
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Initialize SessionManager
        sessionManager = new SessionManager(this);

        // Step 7.1: This is the app entry point
        // Step 7.2: Read login state from SharedPreferences
        checkLoginStatusAndNavigate();
    }

    /**
     * Step 7.3: Conditional navigation logic based on login state
     */
    private void checkLoginStatusAndNavigate() {
        // Add a small delay for better UX
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Step 7.2: Read stored boolean value
                boolean isLoggedIn = sessionManager.isLoggedIn();
                String username = sessionManager.getUsername();

                // Log for debugging
                System.out.println("=== SPLASH ACTIVITY ===");
                System.out.println("Login Status: " + isLoggedIn);
                System.out.println("Username: " + (username != null ? username : "null"));

                Intent intent;

                // Step 7.3: Conditional navigation
                if (isLoggedIn) {
                    // User is logged in → Skip Login screen
                    System.out.println("User is logged in. Redirecting to MainActivity...");
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                } else {
                    // User is not logged in → Show Login screen
                    System.out.println("User is NOT logged in. Redirecting to LoginActivity...");
                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                }

                // Step 7.5: Keep logic lightweight - execute navigation
                startActivity(intent);
                finish(); // Close splash activity so user can't go back
            }
        }, SPLASH_DELAY);
    }
}