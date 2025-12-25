package com.example.echosign.utils;

import android.content.Context;
import android.content.res.Resources;
import java.util.HashMap;
import java.util.Map;

public class SignMapper {

    private Map<String, String> signDictionary;
    private Context context;

    public SignMapper(Context context) {
        this.context = context;
        initializeSignDictionary();
    }

    /**
     * Step 10.2: Initialize word-to-sign mapping dictionary
     */
    private void initializeSignDictionary() {
        signDictionary = new HashMap<>();

        // Basic greetings and common words
        signDictionary.put("hello", "Hand wave greeting");
        signDictionary.put("hi", "Hand wave greeting");
        signDictionary.put("thank", "Hand to chin then forward");
        signDictionary.put("thanks", "Hand to chin then forward");
        signDictionary.put("please", "Circular hand on chest");
        signDictionary.put("sorry", "Fist circling on chest");
        signDictionary.put("help", "Thumb up with other hand support");
        signDictionary.put("yes", "Nodding fist");
        signDictionary.put("no", "Shaking hand");
        signDictionary.put("good", "Hand moving from chin outward");
        signDictionary.put("bad", "Hand moving from chin downward");

        // Common needs
        signDictionary.put("water", "W-shaped hand to mouth");
        signDictionary.put("food", "Hand to mouth");
        signDictionary.put("eat", "Hand to mouth");
        signDictionary.put("drink", "C-shaped hand to mouth");
        signDictionary.put("bathroom", "T-shaped hand shaking");
        signDictionary.put("toilet", "T-shaped hand shaking");

        // Emergency words
        signDictionary.put("help", "Thumb up supported");
        signDictionary.put("emergency", "Crossed arms waving");
        signDictionary.put("doctor", "D on wrist pulse");
        signDictionary.put("hospital", "H on wrist pulse");
        signDictionary.put("pain", "Index fingers twisting");
        signDictionary.put("hurt", "Index fingers twisting");

        // Basic questions
        signDictionary.put("what", "Open hand shaking side to side");
        signDictionary.put("where", "Index finger pointing around");
        signDictionary.put("when", "Index finger circling");
        signDictionary.put("why", "Y-hand touching forehead");
        signDictionary.put("how", "Two hands opening upward");
        signDictionary.put("who", "Index finger circling near mouth");

        // Family
        signDictionary.put("mother", "Thumb on chin");
        signDictionary.put("father", "Thumb on forehead");
        signDictionary.put("mom", "Thumb on chin");
        signDictionary.put("dad", "Thumb on forehead");
        signDictionary.put("family", "F hands circling");

        // Emotions
        signDictionary.put("happy", "Hands brushing up chest");
        signDictionary.put("sad", "Hands down face");
        signDictionary.put("angry", "Claw hands at chest");
        signDictionary.put("love", "Crossed hands on chest");
        signDictionary.put("scared", "Hands shaking at chest");

        // Time
        signDictionary.put("now", "Y-hands downward");
        signDictionary.put("today", "Y-hand to chin then down");
        signDictionary.put("tomorrow", "Hand moving forward from chin");
        signDictionary.put("yesterday", "Thumb over shoulder");

        // Numbers 1-10 (simplified)
        for (int i = 1; i <= 10; i++) {
            signDictionary.put(String.valueOf(i), "Number " + i + " sign");
        }

        System.out.println("SignMapper: Initialized with " + signDictionary.size() + " word mappings");
    }

    /**
     * Check if a word has a sign mapping
     */
    public boolean hasSignForWord(String word) {
        if (word == null || word.trim().isEmpty()) {
            return false;
        }

        String cleanWord = word.toLowerCase().trim();

        // Direct match
        if (signDictionary.containsKey(cleanWord)) {
            return true;
        }

        // Try plural forms
        if (cleanWord.endsWith("s")) {
            String singular = cleanWord.substring(0, cleanWord.length() - 1);
            if (signDictionary.containsKey(singular)) {
                return true;
            }
        }

        // Try common variations
        if (cleanWord.endsWith("ing")) {
            String base = cleanWord.substring(0, cleanWord.length() - 3);
            if (signDictionary.containsKey(base)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get sign information for a word
     */
    public String getSignInfo(String word) {
        if (word == null || word.trim().isEmpty()) {
            return "No sign available";
        }

        String cleanWord = word.toLowerCase().trim();

        // Direct match
        if (signDictionary.containsKey(cleanWord)) {
            return signDictionary.get(cleanWord);
        }

        // Try plural forms
        if (cleanWord.endsWith("s")) {
            String singular = cleanWord.substring(0, cleanWord.length() - 1);
            if (signDictionary.containsKey(singular)) {
                return signDictionary.get(singular) + " (plural)";
            }
        }

        // Try -ing forms
        if (cleanWord.endsWith("ing")) {
            String base = cleanWord.substring(0, cleanWord.length() - 3);
            if (signDictionary.containsKey(base)) {
                return signDictionary.get(base) + " (action)";
            }
        }

        // Step 10.3: Fallback to alphabet spelling
        return "Will be spelled: " + cleanWord.toUpperCase();
    }

    /**
     * Get all mapped words (for debugging)
     */
    public String getAllMappedWords() {
        StringBuilder sb = new StringBuilder();
        sb.append("Sign Mappings (").append(signDictionary.size()).append(" words):\n");

        int count = 0;
        for (Map.Entry<String, String> entry : signDictionary.entrySet()) {
            sb.append(entry.getKey()).append(" → ").append(entry.getValue());
            count++;
            if (count % 5 == 0) {
                sb.append("\n");
            } else {
                sb.append(" | ");
            }
        }

        return sb.toString();
    }

    /**
     * Get the number of mapped signs
     */
    public int getSignCount() {
        return signDictionary.size();
    }
}