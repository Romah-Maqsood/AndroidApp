package com.example.echosign;

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

        // Display user information if available
        displayUserInfo();

        // Logout button click listener
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show logout confirmation
                Toast.makeText(MainActivity.this,
                        "Logout button pressed\n(No logout implemented yet - Step 6)",
                        Toast.LENGTH_SHORT).show();

                System.out.println("MainActivity - Logout button clicked");

                // Note: Logout functionality will be implemented in Step 7
                // For now, just show confirmation but don't actually logout
            }
        });
    }

    /**
     * Display user information from SharedPreferences
     */
    private void displayUserInfo() {
        if (sessionManager.isLoggedIn()) {
            String username = sessionManager.getUsername();
            tvWelcomeUser.setText("Welcome, " + username);

            // Log stored data for debugging
            String storedData = sessionManager.getAllData();
            System.out.println("MainActivity - User is logged in:");
            System.out.println(storedData);
        } else {
            tvWelcomeUser.setText("Welcome, Guest");
            System.out.println("MainActivity - No user logged in (Guest mode)");
        }
    }
}