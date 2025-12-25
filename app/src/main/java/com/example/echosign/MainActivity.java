package com.example.echosign;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.echosign.utils.SessionManager;

public class MainActivity extends AppCompatActivity {

    private TextView tvWelcomeUser;
    private Button btnLogout;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize SessionManager
        sessionManager = new SessionManager(this);

        // Initialize views
        tvWelcomeUser = findViewById(R.id.tvWelcomeUser);
        btnLogout = findViewById(R.id.btnLogout);

        // Check if user is logged in (security check)
        checkLoginStatus();

        // Display user information
        displayUserInfo();

        // Logout button click listener
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Logout the user
                logoutUser();
            }
        });
    }

    /**
     * Check if user is actually logged in
     * If not, redirect to LoginActivity
     */
    private void checkLoginStatus() {
        if (!sessionManager.isLoggedIn()) {
            // User is not logged in, redirect to LoginActivity
            System.out.println("MainActivity - User not logged in. Redirecting to LoginActivity...");

            Toast.makeText(this,
                    "Session expired. Please login again.",
                    Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Close MainActivity
        }
    }

    /**
     * Display user information from SharedPreferences
     */
    private void displayUserInfo() {
        if (sessionManager.isLoggedIn()) {
            String username = sessionManager.getUsername();
            tvWelcomeUser.setText("Welcome, " + username + "!");

            // Log for debugging
            System.out.println("MainActivity - User logged in: " + username);
            System.out.println("MainActivity - Full session data:");
            System.out.println(sessionManager.getAllData());
        }
    }

    /**
     * Logout user and redirect to LoginActivity
     */
    private void logoutUser() {
        // Get username before logging out
        String username = sessionManager.getUsername();

        // Clear session
        sessionManager.logoutUser();

        // Show confirmation
        Toast.makeText(this,
                "Goodbye, " + username + "!\nLogged out successfully.",
                Toast.LENGTH_SHORT).show();

        // Redirect to LoginActivity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Close MainActivity

        System.out.println("MainActivity - User logged out: " + username);
    }

    @Override
    public void onBackPressed() {
        // Step 7.4: Prevent going back to Login screen
        // Instead, minimize the app
        moveTaskToBack(true);
    }
}