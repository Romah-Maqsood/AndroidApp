package com.example.echosign;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.echosign.utils.SignMapper;

import java.util.ArrayList;
import java.util.List;

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
    private Button btnShowSigns;
    private View statusDot;
    private TextView tvStatus;

    // Session Manager
    private SessionManager sessionManager;

    // Speech Recognition
    private SpeechRecognizer speechRecognizer;
    private Intent speechIntent;
    private boolean isListening = false;
    private static final int RECORD_AUDIO_PERMISSION_CODE = 100;

    // Sign Mapping
    private SignMapper signMapper;
    private List<String> wordQueue = new ArrayList<>();
    private int currentWordIndex = 0;
    private boolean isShowingSigns = false;
    private Handler signHandler = new Handler();

    // Animation resources (simulated with drawable states)
    private int[] signAnimations = {
            R.drawable.ic_hello,
            R.drawable.ic_thank_you,
            R.drawable.ic_yes,
            R.drawable.ic_no
//            R.drawable.ic_please,
//            R.drawable.ic_sorry,
//            R.drawable.ic_help,
//            R.drawable.ic_water,
//            R.drawable.ic_food,
//            R.drawable.ic_bathroom
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize SessionManager
        sessionManager = new SessionManager(this);

        // Initialize SignMapper
        signMapper = new SignMapper(this);

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

        System.out.println("MainActivity: Step 10 - Text-to-Sign mapping ready");
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
        btnShowSigns = findViewById(R.id.btnShowSigns);
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
            tvWelcomeUser.setText("Ready to convert speech");
        }
    }

    /**
     * Initialize speech recognition components
     */
    private void initializeSpeechRecognition() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
        speechIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        speechIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...");

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                runOnUiThread(() -> {
                    updateUIForListening(true);
                    tvStatus.setText("Listening... Speak now");
                    statusDot.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                    tvSignDescription.setText("Microphone active - Speaking detected");
                });
            }

            @Override
            public void onBeginningOfSpeech() {
                runOnUiThread(() -> {
                    tvStatus.setText("Speech detected");
                    tvSignDescription.setText("Processing your speech...");
                });
            }

            @Override
            public void onRmsChanged(float rmsdB) {
                // Visual feedback for speech volume
                float scale = Math.min(1.0f, rmsdB / 10);
                ivSignAnimation.setScaleX(1.0f + scale * 0.1f);
                ivSignAnimation.setScaleY(1.0f + scale * 0.1f);
            }

            @Override
            public void onBufferReceived(byte[] buffer) {}

            @Override
            public void onEndOfSpeech() {
                runOnUiThread(() -> {
                    tvStatus.setText("Processing...");
                    statusDot.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
                });
            }

            @Override
            public void onError(int error) {
                runOnUiThread(() -> {
                    updateUIForListening(false);
                    handleSpeechError(error);
                });
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

                        // Step 10.1: Tokenize and process the text for sign mapping
                        processTextForSigns(recognizedText);
                    });
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
                }
            }

            @Override
            public void onEvent(int eventType, Bundle params) {}
        });
    }

    /**
     * Step 10.1: Tokenize and process text for sign mapping
     */
    private void processTextForSigns(String text) {
        // Clear previous queue
        wordQueue.clear();
        currentWordIndex = 0;

        // Tokenize text into words
        String[] words = text.toLowerCase().split("\\s+");

        // Filter out common filler words (Step 10.1)
        List<String> filteredWords = new ArrayList<>();
        for (String word : words) {
            word = word.replaceAll("[^a-zA-Z]", ""); // Remove punctuation
            if (!word.isEmpty() && !isFillerWord(word)) {
                filteredWords.add(word);
            }
        }

        wordQueue.addAll(filteredWords);

        // Update UI to show word count
        tvSignDescription.setText("Ready to show " + wordQueue.size() + " signs");

        // Enable the "Show Signs" button
        btnShowSigns.setEnabled(true);
        btnShowSigns.setBackgroundColor(getResources().getColor(android.R.color.holo_purple));

        // Log for debugging
        System.out.println("Step 10.1: Tokenized text into " + wordQueue.size() + " words");
        System.out.println("Words: " + wordQueue);

        // If there are words, show first word preview
        if (!wordQueue.isEmpty()) {
            showWordPreview(wordQueue.get(0));
        }
    }

    /**
     * Check if word is a filler word
     */
    private boolean isFillerWord(String word) {
        String[] fillerWords = {
                "a", "an", "the", "and", "or", "but", "in", "on", "at", "to",
                "for", "of", "with", "by", "is", "are", "was", "were", "be",
                "been", "being", "have", "has", "had", "do", "does", "did",
                "will", "would", "shall", "should", "may", "might", "must",
                "can", "could", "um", "uh", "ah", "like", "you", "me", "i"
        };

        for (String filler : fillerWords) {
            if (filler.equals(word)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Show preview of a word
     */
    private void showWordPreview(String word) {
        // Check if word has a sign mapping
        if (signMapper.hasSignForWord(word)) {
            String signInfo = signMapper.getSignInfo(word);
            tvCurrentWord.setText(word.toUpperCase());
            tvCurrentWord.setVisibility(View.VISIBLE);
            tvSignDescription.setText("Word: " + word + " → " + signInfo);

            // Visual feedback for mapped word
            ivSignAnimation.setImageResource(getRandomSignAnimation());
            ivSignAnimation.animate().scaleX(1.1f).scaleY(1.1f).setDuration(300).start();
        } else {
            // Show fallback for missing sign
            tvCurrentWord.setText("Spell: " + word.toUpperCase());
            tvCurrentWord.setVisibility(View.VISIBLE);
            tvSignDescription.setText("Word: " + word + " (will be spelled)");

            // Different visual for spelled words
            ivSignAnimation.setImageResource(R.drawable.ic_spell);
            ivSignAnimation.animate().scaleX(0.9f).scaleY(0.9f).setDuration(300).start();
        }
    }

    /**
     * Step 10.4: Display signs sequentially
     */
    private void startShowingSigns() {
        if (wordQueue.isEmpty() || isShowingSigns) {
            return;
        }

        isShowingSigns = true;
        currentWordIndex = 0;

        // Disable controls during animation
        btnStart.setEnabled(false);
        btnStop.setEnabled(false);
        btnClear.setEnabled(false);
        btnShowSigns.setEnabled(false);

        // Update status
        tvStatus.setText("Showing signs...");
        statusDot.setBackgroundColor(getResources().getColor(android.R.color.holo_purple));

        // Start the sequence
        showNextSign();
    }

    /**
     * Show next sign in the sequence
     */
    private void showNextSign() {
        if (currentWordIndex >= wordQueue.size()) {
            // All signs shown
            finishShowingSigns();
            return;
        }

        String currentWord = wordQueue.get(currentWordIndex);

        // Update display
        tvCurrentWord.setText(currentWord.toUpperCase());
        tvCurrentWord.setVisibility(View.VISIBLE);

        // Step 10.2 & 10.3: Get sign animation or fallback
        if (signMapper.hasSignForWord(currentWord)) {
            // Word has a mapped sign
            String signInfo = signMapper.getSignInfo(currentWord);
            tvSignDescription.setText("Sign for: " + currentWord + " (" + signInfo + ")");

            // Show "animation" - in real app this would be GIF/Lottie
            ivSignAnimation.setImageResource(getRandomSignAnimation());

            // Animation effect
            ivSignAnimation.animate()
                    .scaleX(1.3f).scaleY(1.3f)
                    .setDuration(300)
                    .withEndAction(() -> ivSignAnimation.animate()
                            .scaleX(1.0f).scaleY(1.0f)
                            .setDuration(300)
                            .start())
                    .start();

            System.out.println("Step 10.2: Showing sign for word: " + currentWord);
        } else {
            // Step 10.3: Fallback to alphabet spelling
            tvSignDescription.setText("Spelling: " + currentWord);

            // Show spelling animation
            ivSignAnimation.setImageResource(R.drawable.ic_spell);

            // Visual feedback for spelling
            ivSignAnimation.animate()
                    .rotationBy(360)
                    .setDuration(800)
                    .start();

            System.out.println("Step 10.3: Spelling word (no sign): " + currentWord);
        }

        // Log progress
        System.out.println("Step 10.4: Showing sign " + (currentWordIndex + 1) +
                " of " + wordQueue.size() + ": " + currentWord);

        // Schedule next sign after delay
        currentWordIndex++;
        signHandler.postDelayed(this::showNextSign, 1500); // 1.5 seconds per sign
    }

    /**
     * Finish showing signs sequence
     */
    private void finishShowingSigns() {
        isShowingSigns = false;

        // Re-enable controls
        updateUIForListening(false);
        btnShowSigns.setEnabled(true);

        // Update status
        tvStatus.setText("Sign sequence complete");
        statusDot.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
        tvSignDescription.setText(wordQueue.size() + " signs displayed successfully");

        // Reset animation
        tvCurrentWord.setVisibility(View.GONE);
        ivSignAnimation.setImageResource(R.drawable.sign_language_placeholder);
        ivSignAnimation.setScaleX(1.0f);
        ivSignAnimation.setScaleY(1.0f);

        // Show completion message
        Toast.makeText(this, "Sign sequence completed!", Toast.LENGTH_SHORT).show();

        System.out.println("Step 10.4: Sign sequence completed for " + wordQueue.size() + " words");
    }

    /**
     * Get random sign animation (simulated)
     */
    private int getRandomSignAnimation() {
        // In real app, this would return specific animation for each word
        // For demo, return random from our placeholder animations
        return signAnimations[(int) (Math.random() * signAnimations.length)];
    }

    /**
     * Setup UI state with click listeners
     */
    private void setupUIState() {
        // Set initial text
        tvRecognizedText.setText("Speak a sentence like: \"Hello, thank you, please help\"");
        tvEmptyText.setVisibility(View.VISIBLE);

        // Set initial status
        tvStatus.setText("Ready - Tap Start to begin");
        statusDot.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));

        // Logout button
        btnLogout.setOnClickListener(v -> logoutUser());

        // Start button
        btnStart.setOnClickListener(v -> startSpeechRecognition());

        // Stop button
        btnStop.setOnClickListener(v -> stopSpeechRecognition());

        // Clear button
        btnClear.setOnClickListener(v -> clearRecognizedText());

        // Show Signs button (Step 10 feature)
        btnShowSigns.setOnClickListener(v -> {
            if (!wordQueue.isEmpty()) {
                startShowingSigns();
            } else {
                Toast.makeText(this, "No words to convert to signs. Speak first!",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Initial button states
        updateUIForListening(false);
        btnShowSigns.setEnabled(false);
        btnShowSigns.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
    }

    /**
     * Check and request microphone permission
     */
    private void checkMicrophonePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    RECORD_AUDIO_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RECORD_AUDIO_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Microphone permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Microphone permission denied", Toast.LENGTH_LONG).show();
                btnStart.setEnabled(false);
                btnStart.setText("Permission Needed");
                tvStatus.setText("Microphone permission required");
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
            } catch (Exception e) {
                Toast.makeText(this, "Error starting speech recognition: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
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
            ivSignAnimation.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300).start();
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

        // Clear sign queue
        wordQueue.clear();
        currentWordIndex = 0;
        btnShowSigns.setEnabled(false);
        btnShowSigns.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        tvCurrentWord.setVisibility(View.GONE);

        Toast.makeText(this, "Text and sign queue cleared", Toast.LENGTH_SHORT).show();
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
    }

    /**
     * Update UI for listening state
     */
    private void updateUIForListening(boolean listening) {
        isListening = listening;

        btnStart.setEnabled(!listening);
        btnStop.setEnabled(listening);
        btnClear.setEnabled(!listening && !isShowingSigns);
        btnShowSigns.setEnabled(!listening && !isShowingSigns && !wordQueue.isEmpty());

        if (listening) {
            tvStatus.setText("Listening...");
            statusDot.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
            tvCurrentWord.setVisibility(View.VISIBLE);
            tvCurrentWord.setText("Speak now");
        } else {
            tvStatus.setText("Ready");
            statusDot.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            if (!isShowingSigns) {
                tvCurrentWord.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Handle speech recognition errors
     */
    private void handleSpeechError(int errorCode) {
        String errorMessage;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO: errorMessage = "Audio error"; break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                errorMessage = "Microphone permission needed";
                checkMicrophonePermission();
                break;
            case SpeechRecognizer.ERROR_NO_MATCH: errorMessage = "No speech recognized"; break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT: errorMessage = "No speech input"; break;
            default: errorMessage = "Error: " + errorCode;
        }

        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        tvStatus.setText("Error");
        statusDot.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
    }

    /**
     * Logout user
     */
    private void logoutUser() {
        if (isListening) stopSpeechRecognition();

        String username = sessionManager.getUsername();
        sessionManager.logoutUser();

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
        signHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}