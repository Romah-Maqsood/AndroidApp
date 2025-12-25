package com.example.echosign;

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

        // Step 6.2: Save login status on button press
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get user input
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                // Step 6.4: Confirm data is saved with temporary confirmation
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this,
                            "Please enter both username and password",
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Step 6.2: Save login session using SharedPreferences
                    sessionManager.createLoginSession(username, password);

                    // Step 6.4: Show confirmation that data is saved
                    showSaveConfirmation(username);

                    // Step 6.5: Keep app on same screen (NO navigation yet)
                    // We stay on LoginActivity as per Step 6 requirements

                    // Clear fields after saving
                    etUsername.setText("");
                    etPassword.setText("");
                }
            }
        });
    }

    // Step 6.4: Show confirmation that data is saved
    private void showSaveConfirmation(String username) {
        // Get all stored data for confirmation
        String storedData = sessionManager.getAllData();

        // Show toast with saved information
        Toast.makeText(this,
                "Login data saved!\n" +
                        "User: " + username + "\n" +
                        "Check Logcat for details",
                Toast.LENGTH_LONG).show();

        // Log detailed information for debugging
        System.out.println("=== LOGIN DATA SAVED ===");
        System.out.println("Username entered: " + username);
        System.out.println("Stored in SharedPreferences:");
        System.out.println(storedData);
        System.out.println("========================");
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Optional: Check if user is already logged in (for debugging)
        if (sessionManager.isLoggedIn()) {
            String username = sessionManager.getUsername();
            System.out.println("LoginActivity - User already logged in: " + username);
        } else {
            System.out.println("LoginActivity - No user logged in");
        }
    }
}