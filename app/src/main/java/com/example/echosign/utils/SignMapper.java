package com.example.echosign.utils;

import android.content.Context;
import android.content.res.Resources;
import java.util.HashMap;
import java.util.Map;

public class SignMapper {

    private Map<String, String> signDictionary;
    private ASLGlossConverter glossConverter;

    public SignMapper(Context context) {
        this.glossConverter = new ASLGlossConverter();
        initializeSignDictionary();

        // Log ASL Gloss examples for debugging
        System.out.println(glossConverter.getExampleConversions());
    }

    /**
     * Step 11: Process text through ASL Gloss conversion before mapping
     */
    public String processTextWithASLGloss(String englishText) {
        if (englishText == null || englishText.trim().isEmpty()) {
            return "";
        }

        // Convert English to ASL Gloss
        String aslGloss = glossConverter.convertToASLGloss(englishText);

        // Get conversion stats
        System.out.println(glossConverter.getConversionStats(englishText));

        return aslGloss;
    }

    /**
     * Tokenize ASL Gloss text for sign display
     */
    public String[] tokenizeASLGloss(String aslGloss) {
        if (aslGloss == null || aslGloss.trim().isEmpty()) {
            return new String[0];
        }

        return aslGloss.split("\\s+");
    }

    /**
     * Get sign mapping for ASL Gloss word
     */
    public String getSignForGlossWord(String glossWord) {
        if (glossWord == null || glossWord.trim().isEmpty()) {
            return "No sign available";
        }

        String word = glossWord.toLowerCase();

        // Direct match
        if (signDictionary.containsKey(word)) {
            return signDictionary.get(word);
        }

        // Try without common ASL modifiers
        if (word.endsWith("+")) {
            String base = word.substring(0, word.length() - 1);
            if (signDictionary.containsKey(base)) {
                return signDictionary.get(base) + " (modified)";
            }
        }

        // Check for compound words (ASL sometimes uses compounds)
        if (word.contains("_")) {
            String[] parts = word.split("_");
            StringBuilder compoundSign = new StringBuilder();
            for (String part : parts) {
                if (signDictionary.containsKey(part)) {
                    compoundSign.append(signDictionary.get(part)).append(" + ");
                }
            }
            if (compoundSign.length() > 0) {
                return compoundSign.toString();
            }
        }

        // Fallback description
        return "ASL sign for: " + glossWord;
    }

    /**
     * Check if ASL Gloss word has a sign
     */
    public boolean hasSignForGlossWord(String glossWord) {
        if (glossWord == null || glossWord.trim().isEmpty()) {
            return false;
        }

        String word = glossWord.toLowerCase();

        // Check direct match
        if (signDictionary.containsKey(word)) {
            return true;
        }

        // Check without modifiers
        if (word.endsWith("+")) {
            String base = word.substring(0, word.length() - 1);
            return signDictionary.containsKey(base);
        }

        // Check compound words
        if (word.contains("_")) {
            String[] parts = word.split("_");
            for (String part : parts) {
                if (signDictionary.containsKey(part)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Initialize sign dictionary (updated for ASL Gloss)
     */
    private void initializeSignDictionary() {
        signDictionary = new HashMap<>();

        // Basic ASL Gloss mappings
        signDictionary.put("hello", "Hand wave greeting");
        signDictionary.put("hi", "Hand wave greeting");
        signDictionary.put("thank", "Hand to chin then forward");
        signDictionary.put("you", "Point forward");
        signDictionary.put("help", "Thumb up with other hand support");
        signDictionary.put("please", "Circular hand on chest");
        signDictionary.put("sorry", "Fist circling on chest");
        signDictionary.put("yes", "Nodding fist");
        signDictionary.put("no", "Shaking hand");

        // Pronouns (ASL uses pointing)
        signDictionary.put("i", "Point to self");
        signDictionary.put("me", "Point to self");
        signDictionary.put("my", "Flat hand to chest");
        signDictionary.put("you", "Point forward");
        signDictionary.put("your", "Point forward + flat hand");
        signDictionary.put("he", "Point to side (male)");
        signDictionary.put("she", "Point to side (female)");
        signDictionary.put("it", "Point to object");
        signDictionary.put("we", "Point to self then arc");
        signDictionary.put("they", "Point to multiple");

        // Common needs
        signDictionary.put("water", "W-shaped hand to mouth");
        signDictionary.put("food", "Hand to mouth");
        signDictionary.put("eat", "Hand to mouth");
        signDictionary.put("drink", "C-shaped hand to mouth");
        signDictionary.put("bathroom", "T-shaped hand shaking");
        signDictionary.put("toilet", "T-shaped hand shaking");

        // Emergency/health
        signDictionary.put("doctor", "D on wrist pulse");
        signDictionary.put("hospital", "H on wrist pulse");
        signDictionary.put("pain", "Index fingers twisting");
        signDictionary.put("hurt", "Index fingers twisting");
        signDictionary.put("sick", "Forehead then stomach");

        // Question words
        signDictionary.put("what", "Open hand shaking side to side");
        signDictionary.put("where", "Index finger pointing around");
        signDictionary.put("when", "Index finger circling");
        signDictionary.put("why", "Y-hand touching forehead");
        signDictionary.put("how", "Two hands opening upward");
        signDictionary.put("who", "Index finger circling near mouth");

        // Time references
        signDictionary.put("now", "Y-hands downward");
        signDictionary.put("today", "Y-hand to chin then down");
        signDictionary.put("tomorrow", "Hand moving forward from chin");
        signDictionary.put("yesterday", "Thumb over shoulder");
        signDictionary.put("time", "Index finger tapping wrist");

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

        // Actions
        signDictionary.put("want", "Claw hands pulling inward");
        signDictionary.put("need", "X-hand on chest");
        signDictionary.put("go", "Point forward then move");
        signDictionary.put("come", "Wave toward self");
        signDictionary.put("see", "V-fingers from eyes");
        signDictionary.put("look", "V-fingers from eyes");
        signDictionary.put("know", "Tap forehead");
        signDictionary.put("understand", "Index finger to forehead then fist");
        signDictionary.put("think", "Index finger to temple");
        signDictionary.put("feel", "Middle finger up chest");

        // Places
        signDictionary.put("home", "Hand to cheek then flat");
        signDictionary.put("school", "Clapping hands");
        signDictionary.put("work", "S-hands pounding");
        signDictionary.put("store", "Money then give");

        // Numbers 1-10
        for (int i = 1; i <= 10; i++) {
            signDictionary.put(String.valueOf(i), "Number " + i + " sign");
        }

        System.out.println("SignMapper: Initialized with " + signDictionary.size() + " ASL sign mappings");
    }

    /**
     * Get all mapped words (for debugging)
     */
    public String getAllMappedWords() {
        StringBuilder sb = new StringBuilder();
        sb.append("ASL Sign Mappings (").append(signDictionary.size()).append(" words):\n");

        int count = 0;
        for (Map.Entry<String, String> entry : signDictionary.entrySet()) {
            sb.append(entry.getKey().toUpperCase()).append(" → ").append(entry.getValue());
            count++;
            if (count % 4 == 0) {
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

    // Legacy methods (compatibility with existing code)
    public boolean hasSignForWord(String word) {
        return hasSignForGlossWord(word);
    }

    public String getSignInfo(String word) {
        return getSignForGlossWord(word);
    }
}