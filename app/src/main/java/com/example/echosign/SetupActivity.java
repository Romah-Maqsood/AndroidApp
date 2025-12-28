package com.example.echosign;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.echosign.utils.SessionManager;

public class SetupActivity extends AppCompatActivity {

    private RadioGroup rgSignMode, rgCaptions, rgUsage;
    private Button btnSaveSetup;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        sessionManager = new SessionManager(this);

        rgSignMode = findViewById(R.id.rgSignMode);
        rgCaptions = findViewById(R.id.rgCaptions);
        rgUsage = findViewById(R.id.rgUsage);
        btnSaveSetup = findViewById(R.id.btnSaveSetup);

        btnSaveSetup.setOnClickListener(v -> {
            savePreferences();

            // Mark setup as complete
            sessionManager.setSetupComplete(true);

            // Navigate to MainActivity
            Intent intent = new Intent(SetupActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void savePreferences() {
        // Get selected sign mode
        int selectedSignModeId = rgSignMode.getCheckedRadioButtonId();
        RadioButton rbSignMode = findViewById(selectedSignModeId);
        String signMode = rbSignMode.getText().toString();

        // Get selected captions preference
        int selectedCaptionsId = rgCaptions.getCheckedRadioButtonId();
        RadioButton rbCaptions = findViewById(selectedCaptionsId);
        boolean captionsEnabled = rbCaptions.getText().toString().equalsIgnoreCase("Yes");

        // Get selected usage purpose
        int selectedUsageId = rgUsage.getCheckedRadioButtonId();
        RadioButton rbUsage = findViewById(selectedUsageId);
        String usagePurpose = rbUsage.getText().toString();

        // Save to SessionManager
        sessionManager.saveUserPreferences(signMode, captionsEnabled, usagePurpose);

        Toast.makeText(this, "Preferences Saved!", Toast.LENGTH_SHORT).show();
    }
}