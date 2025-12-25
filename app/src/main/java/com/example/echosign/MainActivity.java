package com.example.echosign;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.echosign.utils.SessionManager;

public class MainActivity extends AppCompatActivity {

    // Header Section
    private TextView tvWelcomeUser;
    private Button btnLogout;

    // Recognized Text Area
    private TextView tvRecognizedText;
    private TextView tvEmptyText;

    // Sign Display Area
    private ImageView ivSignAnimation;
    private ProgressBar pbLoading;
    private TextView tvCurrentWord;
    private TextView tvSignDescription;

    // Control Area
    private Button btnStart;
    private Button btnStop;
    private Button btnClear;
    private View statusDot;
    private TextView tvStatus;

    // Session Manager
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize SessionManager
        sessionManager = new SessionManager(this);

        // Initialize all UI components
        initializeViews();

        // Display user information
        displayUserInfo();

        // Step 8: No click listeners yet - just UI setup
        setupUIState();

        // Log UI setup completion
        System.out.println("MainActivity: UI setup complete for Step 8");
        System.out.println("MainActivity: No functionality attached yet");
    }

    /**
     * Initialize all view components
     */
    private void initializeViews() {
        // Header Section
        tvWelcomeUser = findViewById(R.id.tvWelcomeUser);
        btnLogout = findViewById(R.id.btnLogout);

        // Recognized Text Area
        tvRecognizedText = findViewById(R.id.tvRecognizedText);
        tvEmptyText = findViewById(R.id.tvEmptyText);

        // Sign Display Area
        ivSignAnimation = findViewById(R.id.ivSignAnimation);
        pbLoading = findViewById(R.id.pbLoading);
        tvCurrentWord = findViewById(R.id.tvCurrentWord);
        tvSignDescription = findViewById(R.id.tvSignDescription);

        // Control Area
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
        btnClear = findViewById(R.id.btnClear);
        statusDot = findViewById(R.id.statusDot);
        tvStatus = findViewById(R.id.tvStatus);
    }

    /**
     * Display user information
     */
    private void displayUserInfo() {
        if (sessionManager.isLoggedIn()) {
            String username = sessionManager.getUsername();
            tvWelcomeUser.setText("Welcome, " + username);
        } else {
            // This shouldn't happen, but just in case
            tvWelcomeUser.setText("Ready to convert speech");
        }
    }

    /**
     * Setup initial UI state (Step 8 - visual only)
     */
    private void setupUIState() {
        // Set initial text
        tvRecognizedText.setText("Speech-to-text output will appear here.\n\n" +
                "Example: \"Hello, my name is Alex\"");

        // Set placeholder description
        tvSignDescription.setText("Sign language animations will display here");

        // Set initial status
        tvStatus.setText("Ready - Tap 'Start Listening' to begin");

        // Logout button (still functional from previous steps)
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        // Step 8: Do NOT add click listeners to control buttons yet
        // These are just visual placeholders for now
        // We explicitly set them to null to ensure no functionality
        btnStart.setOnClickListener(null);
        btnStop.setOnClickListener(null);
        btnClear.setOnClickListener(null);

        // Disable the buttons to show they're not functional
        btnStart.setEnabled(false);
        btnStop.setEnabled(false);
        btnClear.setEnabled(false);

        // Change button colors to show they're disabled
        btnStart.setBackgroundColor(0xFFA5D6A7); // Light green
        btnStop.setBackgroundColor(0xFFEF9A9A);  // Light red
        btnClear.setBackgroundColor(0xFFFFCC80); // Light orange

        // Log that buttons are not functional yet
        System.out.println("MainActivity: Control buttons are visual only (Step 8)");
        System.out.println("MainActivity: Start, Stop, Clear buttons are disabled");
    }

    /**
     * Logout user (kept from previous steps)
     */
    private void logoutUser() {
        String username = sessionManager.getUsername();
        sessionManager.logoutUser();

        // Navigate to LoginActivity
        finish();

        System.out.println("MainActivity: User logged out - " + username);
    }

    @Override
    public void onBackPressed() {
        // Minimize the app instead of going back
        moveTaskToBack(true);
    }
}