package com.example.echosign.utils;

import android.content.Context;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Updated SignMapper that uses the new ASL Dictionary
 */
public class SignMapper {

    private ASLDictionary aslDictionary;
    private ASLGlossConverter glossConverter;

    public SignMapper(Context context) {
        this.aslDictionary = new ASLDictionary(context);
        this.glossConverter = new ASLGlossConverter();

        // Log dictionary statistics
        System.out.println(aslDictionary.getDictionaryStats());
        System.out.println("\n" + aslDictionary.getAllSigns());
    }

    /**
     * Process text through ASL Gloss conversion
     */
    public String processTextWithASLGloss(String englishText) {
        if (englishText == null || englishText.trim().isEmpty()) {
            return "";
        }

        return glossConverter.convertToASLGloss(englishText);
    }

    /**
     * Tokenize ASL Gloss text
     */
    public String[] tokenizeASLGloss(String aslGloss) {
        if (aslGloss == null || aslGloss.trim().isEmpty()) {
            return new String[0];
        }

        return aslGloss.split("\\s+");
    }

    /**
     * Check if ASL Gloss word has a sign in the dictionary
     */
    public boolean hasSignForGlossWord(String glossWord) {
        if (glossWord == null || glossWord.trim().isEmpty()) {
            return false;
        }

        // Clean the word (remove punctuation, etc.)
        String cleanWord = glossWord.replaceAll("[^A-Za-z]", "").toUpperCase();

        // Check if it's in the dictionary
        if (aslDictionary.hasSign(cleanWord)) {
            return true;
        }

        // Check for common variations
        if (cleanWord.endsWith("S")) {
            String singular = cleanWord.substring(0, cleanWord.length() - 1);
            if (aslDictionary.hasSign(singular)) {
                return true;
            }
        }

        // Check for compound words
        if (cleanWord.contains("_")) {
            String[] parts = cleanWord.split("_");
            for (String part : parts) {
                if (aslDictionary.hasSign(part)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Get detailed sign information for ASL Gloss word
     */
    public String getSignInfo(String glossWord) {
        if (glossWord == null || glossWord.trim().isEmpty()) {
            return "No sign available";
        }

        String cleanWord = glossWord.replaceAll("[^A-Za-z]", "").toUpperCase();

        // Try to get from dictionary
        ASLDictionary.ASLSign sign = aslDictionary.getSign(cleanWord);

        if (sign != null) {
            return sign.getDescription();
        }

        // Try singular form for plurals
        if (cleanWord.endsWith("S")) {
            String singular = cleanWord.substring(0, cleanWord.length() - 1);
            sign = aslDictionary.getSign(singular);
            if (sign != null) {
                return sign.getDescription() + " (plural)";
            }
        }

        // Try common variations
        Set<String> variations = getWordVariations(cleanWord);
        for (String variation : variations) {
            sign = aslDictionary.getSign(variation);
            if (sign != null) {
                return sign.getDescription() + " (related to: " + variation + ")";
            }
        }

        // Fallback - will be fingerspelled
        return "Will be fingerspelled: " + cleanWord;
    }

    /**
     * Get sign details object for richer information
     */
    public ASLDictionary.ASLSign getSignDetails(String glossWord) {
        if (glossWord == null || glossWord.trim().isEmpty()) {
            return null;
        }

        String cleanWord = glossWord.replaceAll("[^A-Za-z]", "").toUpperCase();
        return aslDictionary.getSign(cleanWord);
    }

    /**
     * Get common variations of a word
     */
    private Set<String> getWordVariations(String word) {
        Set<String> variations = new HashSet<>();

        variations.add(word.toLowerCase());

        // Common verb forms
        if (word.endsWith("ING")) {
            variations.add(word.substring(0, word.length() - 3));
        }

        if (word.endsWith("ED")) {
            variations.add(word.substring(0, word.length() - 2));
        }

        // Common prefixes/suffixes
        String[] prefixes = {"RE", "UN", "PRE", "DIS"};
        String[] suffixes = {"FUL", "LESS", "ABLE", "MENT"};

        for (String prefix : prefixes) {
            if (word.startsWith(prefix) && word.length() > prefix.length()) {
                variations.add(word.substring(prefix.length()));
            }
        }

        for (String suffix : suffixes) {
            if (word.endsWith(suffix) && word.length() > suffix.length()) {
                variations.add(word.substring(0, word.length() - suffix.length()));
            }
        }

        return variations;
    }

    /**
     * Get dictionary statistics
     */
    public String getDictionaryInfo() {
        return aslDictionary.getDictionaryStats();
    }

    /**
     * Get example sentences
     */
    public String[] getExampleSentences() {
        return aslDictionary.getExampleSentences();
    }

    /**
     * Get all categories
     */
    public String[] getCategories() {
        return aslDictionary.getCategories();
    }

    /**
     * Legacy method for compatibility
     */
    public boolean hasSignForWord(String word) {
        return hasSignForGlossWord(word);
    }
}