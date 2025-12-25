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
    private TextView tvEnglishText;
    private TextView tvASLGlossText;
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

    // Sign Mapping with ASL Gloss
    private SignMapper signMapper;
    private List<String> wordQueue = new ArrayList<>();
    private List<String> englishWords = new ArrayList<>();
    private List<String> aslGlossWords = new ArrayList<>();
    private int currentWordIndex = 0;
    private boolean isShowingSigns = false;
    private Handler signHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize SessionManager
        sessionManager = new SessionManager(this);

        // Initialize SignMapper (now includes ASL Gloss converter)
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

        System.out.println("MainActivity: Step 11 - ASL Gloss Conversion ready");
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
        tvEnglishText = findViewById(R.id.tvEnglishText);
        tvASLGlossText = findViewById(R.id.tvASLGlossText);
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
                runOnUiThread(() -> {
                    float scale = Math.min(1.0f, rmsdB / 10);
                    ivSignAnimation.setScaleX(1.0f + scale * 0.1f);
                    ivSignAnimation.setScaleY(1.0f + scale * 0.1f);
                });
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

                        // Step 11: Process text through ASL Gloss conversion
                        processTextWithASLGloss(recognizedText);
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
     * Step 11: Process text through ASL Gloss conversion
     */
    private void processTextWithASLGloss(String englishText) {
        // Clear previous queues
        wordQueue.clear();
        englishWords.clear();
        aslGlossWords.clear();
        currentWordIndex = 0;

        // Store English text
        tvEnglishText.setText("English: " + englishText);

        // Step 11: Convert English to ASL Gloss
        String aslGloss = signMapper.processTextWithASLGloss(englishText);

        // Display ASL Gloss
        tvASLGlossText.setText("ASL Gloss: " + aslGloss);
        tvASLGlossText.setVisibility(View.VISIBLE);

        // Tokenize ASL Gloss for display
        String[] aslTokens = signMapper.tokenizeASLGloss(aslGloss);
        for (String token : aslTokens) {
            wordQueue.add(token);
            aslGlossWords.add(token);
        }

        // Tokenize English for comparison
        String[] engTokens = englishText.split("\\s+");
        for (String token : engTokens) {
            englishWords.add(token);
        }

        // Update UI to show ASL Gloss word count
        int englishCount = englishWords.size();
        int aslCount = aslGlossWords.size();
        tvSignDescription.setText("ASL Gloss: " + aslCount + " words (from " + englishCount + " English)");

        // Enable the "Show Signs" button
        btnShowSigns.setEnabled(true);
        btnShowSigns.setBackgroundColor(getResources().getColor(android.R.color.holo_purple));

        // Log for debugging
        System.out.println("Step 11: English → ASL Gloss Conversion");
        System.out.println("English: " + englishText);
        System.out.println("ASL Gloss: " + aslGloss);
        System.out.println("English words: " + englishCount + ", ASL words: " + aslCount);

        // If there are words, show first word preview
        if (!wordQueue.isEmpty()) {
            showWordPreview(wordQueue.get(0));
        }
    }

    /**
     * Show preview of an ASL Gloss word
     */
    private void showWordPreview(String glossWord) {
        // Check if word has a sign mapping
        if (signMapper.hasSignForGlossWord(glossWord)) {
            String signInfo = signMapper.getSignForGlossWord(glossWord);
            tvCurrentWord.setText(glossWord);
            tvCurrentWord.setVisibility(View.VISIBLE);
            tvSignDescription.setText("ASL: " + glossWord + " → " + signInfo);

            // Visual feedback for mapped word
            ivSignAnimation.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
            ivSignAnimation.animate().scaleX(1.1f).scaleY(1.1f).setDuration(300).start();
        } else {
            // Show fallback for missing sign
            tvCurrentWord.setText("Spell: " + glossWord);
            tvCurrentWord.setVisibility(View.VISIBLE);
            tvSignDescription.setText("ASL: " + glossWord + " (will be spelled)");

            // Different visual for spelled words
            ivSignAnimation.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
            ivSignAnimation.animate().scaleX(0.9f).scaleY(0.9f).setDuration(300).start();
        }
    }

    /**
     * Display signs sequentially
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
        tvStatus.setText("Showing ASL signs...");
        statusDot.setBackgroundColor(getResources().getColor(android.R.color.holo_purple));

        // Start the sequence
        showNextSign();
    }

    /**
     * Show next ASL sign in the sequence
     */
    private void showNextSign() {
        if (currentWordIndex >= wordQueue.size()) {
            // All signs shown
            finishShowingSigns();
            return;
        }

        String currentGlossWord = wordQueue.get(currentWordIndex);

        // Update display with ASL Gloss word
        tvCurrentWord.setText(currentGlossWord);
        tvCurrentWord.setVisibility(View.VISIBLE);

        // Get sign for ASL Gloss word
        if (signMapper.hasSignForGlossWord(currentGlossWord)) {
            // ASL Gloss word has a mapped sign
            String signInfo = signMapper.getSignForGlossWord(currentGlossWord);
            tvSignDescription.setText("ASL Sign: " + currentGlossWord + " (" + signInfo + ")");

            // Show "animation" - use color change for visual feedback
            ivSignAnimation.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));

            // Animation effect
            ivSignAnimation.animate()
                    .scaleX(1.3f).scaleY(1.3f)
                    .setDuration(300)
                    .withEndAction(() -> ivSignAnimation.animate()
                            .scaleX(1.0f).scaleY(1.0f)
                            .setDuration(300)
                            .start())
                    .start();

            System.out.println("Showing ASL sign for: " + currentGlossWord);
        } else {
            // Fallback to alphabet spelling for ASL Gloss word
            tvSignDescription.setText("Spelling ASL: " + currentGlossWord);

            // Show spelling visual feedback
            ivSignAnimation.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));

            // Visual feedback for spelling
            ivSignAnimation.animate()
                    .rotationBy(360)
                    .setDuration(800)
                    .start();

            System.out.println("Spelling ASL word: " + currentGlossWord);
        }

        // Log progress
        System.out.println("ASL Sign " + (currentWordIndex + 1) + " of " + wordQueue.size() + ": " + currentGlossWord);

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
        tvStatus.setText("ASL sequence complete");
        statusDot.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
        tvSignDescription.setText(wordQueue.size() + " ASL signs displayed");

        // Reset animation
        tvCurrentWord.setVisibility(View.GONE);
        ivSignAnimation.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        ivSignAnimation.setScaleX(1.0f);
        ivSignAnimation.setScaleY(1.0f);
        ivSignAnimation.setRotation(0f);

        // Show completion message
        Toast.makeText(this, "ASL sign sequence completed!", Toast.LENGTH_SHORT).show();

        System.out.println("ASL sign sequence completed for " + wordQueue.size() + " words");
    }

    /**
     * Setup UI state with click listeners
     */
    private void setupUIState() {
        // Set initial text
        tvRecognizedText.setText("Speak English: \"Thank you for your help\"");
        tvEnglishText.setVisibility(View.GONE);
        tvASLGlossText.setVisibility(View.GONE);
        tvEmptyText.setVisibility(View.VISIBLE);

        // Set initial status
        tvStatus.setText("Ready - Tap Start to speak");
        statusDot.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));

        // Set initial animation background
        ivSignAnimation.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));

        // Logout button
        btnLogout.setOnClickListener(v -> logoutUser());

        // Start button
        btnStart.setOnClickListener(v -> startSpeechRecognition());

        // Stop button
        btnStop.setOnClickListener(v -> stopSpeechRecognition());

        // Clear button
        btnClear.setOnClickListener(v -> clearRecognizedText());

        // Show Signs button
        btnShowSigns.setOnClickListener(v -> {
            if (!wordQueue.isEmpty()) {
                startShowingSigns();
            } else {
                Toast.makeText(this, "No ASL words to show. Speak first!", Toast.LENGTH_SHORT).show();
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
                tvRecognizedText.setText("Listening... Speak English now ▌");
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
        tvEnglishText.setVisibility(View.GONE);
        tvASLGlossText.setVisibility(View.GONE);
        tvEmptyText.setVisibility(View.VISIBLE);
        tvEmptyText.setText("Text cleared. Tap Start to speak again.");
        tvSignDescription.setText("Ready for English speech input");

        // Clear all queues
        wordQueue.clear();
        englishWords.clear();
        aslGlossWords.clear();
        currentWordIndex = 0;
        btnShowSigns.setEnabled(false);
        btnShowSigns.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        tvCurrentWord.setVisibility(View.GONE);

        // Reset animation
        ivSignAnimation.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        ivSignAnimation.setScaleX(1.0f);
        ivSignAnimation.setScaleY(1.0f);
        ivSignAnimation.setRotation(0f);

        Toast.makeText(this, "Text and ASL queue cleared", Toast.LENGTH_SHORT).show();
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
            tvStatus.setText("Listening English...");
            statusDot.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
            tvCurrentWord.setVisibility(View.VISIBLE);
            tvCurrentWord.setText("Speak English");
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