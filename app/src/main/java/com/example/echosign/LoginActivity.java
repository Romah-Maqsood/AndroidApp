package com.example.echosign;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                showTemporaryConfirmation(username, password);
            }
        });
    }

    private void showTemporaryConfirmation(String username, String password) {
        String message = "Input captured:\nUsername: " + username
                + "\nPassword: " + maskPassword(password);

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        System.out.println("LoginActivity - Username: " + username);
        System.out.println("LoginActivity - Password length: " + password.length());
    }

    private String maskPassword(String password) {
        if (password.isEmpty()) {
            return "[empty]";
        }
        return "*".repeat(password.length());
    }
}