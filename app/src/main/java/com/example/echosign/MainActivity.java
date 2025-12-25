package com.example.echosign;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // UI element - declared but not used functionally
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize view (visual only - no functionality)
        btnLogout = findViewById(R.id.btnLogout);

        // Step 4: No click listeners, no navigation, no logic
        // Button exists visually but does nothing
    }
}