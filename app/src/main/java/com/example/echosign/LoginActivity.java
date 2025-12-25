package com.example.echosign;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.echosign.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize SessionManager
        sessionManager = new SessionManager(this);

        // Initialize views
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        // Check if user is already logged in (just for logging)
        logCurrentStatus();

        // Login button click listener
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get user input
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this,
                            "Please enter both username and password",
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Save login session
                    sessionManager.createLoginSession(username, password);

                    // Show confirmation
                    Toast.makeText(LoginActivity.this,
                            "Login successful!\nWelcome, " + username,
                            Toast.LENGTH_SHORT).show();

                    // Step 7.4: Navigate to MainActivity and prevent back navigation
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // Close LoginActivity so user can't go back
                }
            }
        });
    }

    /**
     * Log current status for debugging
     */
    private void logCurrentStatus() {
        boolean isLoggedIn = sessionManager.isLoggedIn();
        System.out.println("LoginActivity - Current login status: " + isLoggedIn);

        if (isLoggedIn) {
            // This shouldn't happen in Step 7, but log if it does
            String username = sessionManager.getUsername();
            System.out.println("LoginActivity - User already logged in: " + username);
            System.out.println("LoginActivity - This screen shouldn't show if user is logged in!");
        }
    }
}