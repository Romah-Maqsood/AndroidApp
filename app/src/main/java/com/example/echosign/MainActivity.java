package com.example.echosign;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // UI element identified for planning
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize view (for reference in planning)
        btnLogout = findViewById(R.id.btnLogout);

        // No functional logic added - this is Step 3: UI Planning only
        // Logout logic will be implemented in Step 4
    }
}