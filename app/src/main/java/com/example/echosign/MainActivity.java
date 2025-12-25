package com.example.echosign;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.echosign.utils.SessionManager;

import java.util.ArrayList;

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

    // Speech Recognition
    private SpeechRecognizer speechRecognizer;
    private Intent speechIntent;
    private boolean isListening = false;
    private static final int RECORD_AUDIO_PERMISSION_CODE = 100;

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

        // Initialize speech recognition
        initializeSpeechRecognition();

        // Setup UI state
        setupUIState();

        // Check microphone permission
        checkMicrophonePermission();

        System.out.println("MainActivity: Speech-to-Text ready for Step 9");
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
     * Initialize speech recognition components
     */
    private void initializeSpeechRecognition() {
        // Create speech recognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        // Create speech recognition intent
        speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
        speechIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        speechIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...");

        // Set up recognition listener
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                runOnUiThread(() -> {
                    updateUIForListening(true);
                    tvStatus.setText("Listening... Speak now");
                    statusDot.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                    tvSignDescription.setText("Microphone active - Speaking detected");
                });
                System.out.println("SpeechRecognition: Ready for speech");
            }

            @Override
            public void onBeginningOfSpeech() {
                runOnUiThread(() -> {
                    tvStatus.setText("Speech detected");
                    tvSignDescription.setText("Processing your speech...");
                });
                System.out.println("SpeechRecognition: Beginning of speech");
            }

            @Override
            public void onRmsChanged(float rmsdB) {
                // Visual feedback for speech volume
                float scale = Math.min(1.0f, rmsdB / 10);
                ivSignAnimation.setScaleX(1.0f + scale * 0.1f);
                ivSignAnimation.setScaleY(1.0f + scale * 0.1f);
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
                // Not used
            }

            @Override
            public void onEndOfSpeech() {
                runOnUiThread(() -> {
                    tvStatus.setText("Processing...");
                    statusDot.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
                });
                System.out.println("SpeechRecognition: End of speech");
            }

            @Override
            public void onError(int error) {
                runOnUiThread(() -> {
                    updateUIForListening(false);
                    handleSpeechError(error);
                });
                System.out.println("SpeechRecognition: Error " + error);
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    String recognizedText = matches.get(0);
                    runOnUiThread(() -> {
                        displayRecognizedText(recognizedText);
                        updateUIForListening(false);
                        tvStatus.setText("Recognition complete");
                        statusDot.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                        tvSignDescription.setText("Text captured successfully!");
                    });
                    System.out.println("SpeechRecognition: Results - " + recognizedText);
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                ArrayList<String> matches = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    String partialText = matches.get(0);
                    runOnUiThread(() -> {
                        tvRecognizedText.setText(partialText + " ▌");
                        tvSignDescription.setText("Processing: \"" + partialText + "\"");
                    });
                    System.out.println("SpeechRecognition: Partial - " + partialText);
                }
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
                // Not used
            }
        });
    }

    /**
     * Setup UI state with click listeners
     */
    private void setupUIState() {
        // Set initial text
        tvRecognizedText.setText("Click 'Start Listening' and speak into your microphone.\n\nYour speech will appear here as text.");
        tvEmptyText.setVisibility(View.VISIBLE);

        // Set placeholder description
        tvSignDescription.setText("Ready for speech input");

        // Set initial status
        tvStatus.setText("Ready - Tap Start to begin");
        statusDot.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));

        // Logout button
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        // Start button click listener
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSpeechRecognition();
            }
        });

        // Stop button click listener
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSpeechRecognition();
            }
        });

        // Clear button click listener
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearRecognizedText();
            }
        });

        // Initial button states
        updateUIForListening(false);
    }

    /**
     * Check and request microphone permission
     */
    private void checkMicrophonePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            // Request permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    RECORD_AUDIO_PERMISSION_CODE);
        } else {
            System.out.println("Microphone permission already granted");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RECORD_AUDIO_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Microphone permission granted", Toast.LENGTH_SHORT).show();
                System.out.println("Microphone permission granted by user");
            } else {
                Toast.makeText(this, "Microphone permission denied. Speech recognition won't work.",
                        Toast.LENGTH_LONG).show();
                btnStart.setEnabled(false);
                btnStart.setText("Permission Needed");
                tvStatus.setText("Microphone permission required");
                System.out.println("Microphone permission denied by user");
            }
        }
    }

    /**
     * Start speech recognition
     */
    private void startSpeechRecognition() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Microphone permission required", Toast.LENGTH_SHORT).show();
            checkMicrophonePermission();
            return;
        }

        if (!isListening) {
            try {
                speechRecognizer.startListening(speechIntent);
                isListening = true;
                updateUIForListening(true);
                tvRecognizedText.setText("Listening... Speak now ▌");
                tvEmptyText.setVisibility(View.GONE);

                // Visual feedback
                ivSignAnimation.animate().scaleX(1.2f).scaleY(1.2f).setDuration(300).start();

                System.out.println("Speech recognition started");
            } catch (Exception e) {
                Toast.makeText(this, "Error starting speech recognition: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
                System.out.println("Error starting speech recognition: " + e.getMessage());
            }
        }
    }

    /**
     * Stop speech recognition
     */
    private void stopSpeechRecognition() {
        if (isListening) {
            speechRecognizer.stopListening();
            isListening = false;
            updateUIForListening(false);

            // Visual feedback
            ivSignAnimation.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300).start();

            System.out.println("Speech recognition stopped");
        }
    }

    /**
     * Clear recognized text
     */
    private void clearRecognizedText() {
        tvRecognizedText.setText("");
        tvEmptyText.setVisibility(View.VISIBLE);
        tvEmptyText.setText("Text cleared. Tap Start to speak again.");
        tvSignDescription.setText("Ready for new speech input");
        Toast.makeText(this, "Text cleared", Toast.LENGTH_SHORT).show();
        System.out.println("Recognized text cleared");
    }

    /**
     * Display recognized text
     */
    private void displayRecognizedText(String text) {
        tvRecognizedText.setText(text);
        tvEmptyText.setVisibility(View.GONE);

        // Show success animation
        tvRecognizedText.animate().alpha(0.7f).setDuration(200)
                .withEndAction(() -> tvRecognizedText.animate().alpha(1.0f).setDuration(200).start())
                .start();

        // Update word count
        int wordCount = text.trim().isEmpty() ? 0 : text.trim().split("\\s+").length;
        tvSignDescription.setText(wordCount + " words recognized");
    }

    /**
     * Update UI for listening state
     */
    private void updateUIForListening(boolean listening) {
        isListening = listening;

        // Update button states
        btnStart.setEnabled(!listening);
        btnStop.setEnabled(listening);
        btnClear.setEnabled(!listening);

        // Update button colors through background tint
        btnStart.setBackgroundTintList(listening ?
                null : getColorStateList(android.R.color.holo_green_dark));
        btnStop.setBackgroundTintList(listening ?
                getColorStateList(android.R.color.holo_red_dark) : null);

        // Update status
        if (listening) {
            tvStatus.setText("Listening...");
            statusDot.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));

            // Animation feedback
            tvCurrentWord.setVisibility(View.VISIBLE);
            tvCurrentWord.setText("Speak now");
        } else {
            tvStatus.setText("Ready");
            statusDot.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));

            // Reset animation
            tvCurrentWord.setVisibility(View.GONE);
        }
    }

    /**
     * Handle speech recognition errors
     */
    private void handleSpeechError(int errorCode) {
        String errorMessage;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                errorMessage = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                errorMessage = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                errorMessage = "Insufficient permissions";
                checkMicrophonePermission();
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                errorMessage = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                errorMessage = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                errorMessage = "No speech recognized. Please try again.";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                errorMessage = "Recognition service busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                errorMessage = "Server error";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                errorMessage = "No speech input detected";
                break;
            default:
                errorMessage = "Unknown error: " + errorCode;
        }

        Toast.makeText(this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
        tvStatus.setText("Error: " + errorMessage.substring(0, Math.min(20, errorMessage.length())) + "...");
        statusDot.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));

        System.out.println("Speech recognition error: " + errorMessage);
    }

    /**
     * Logout user
     */
    private void logoutUser() {
        String username = sessionManager.getUsername();
        sessionManager.logoutUser();

        // Stop speech recognition if active
        if (isListening) {
            stopSpeechRecognition();
        }

        // Navigate to LoginActivity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();

        System.out.println("MainActivity: User logged out - " + username);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
    }

    @Override
    public void onBackPressed() {
        // Minimize the app instead of going back
        moveTaskToBack(true);
    }
}