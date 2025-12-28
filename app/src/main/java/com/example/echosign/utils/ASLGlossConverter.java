package com.example.echosign.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Step 11: Converts English text to ASL Gloss format
 * ASL Gloss is a written representation of ASL that uses:
 * - Uppercase English words
 * - Simplified sentence structure
 * - Removes unnecessary English grammar
 */
public class ASLGlossConverter {

    // Words to remove from English (English grammar words not used in ASL)
    private static final Set<String> REMOVE_WORDS = new HashSet<>(Arrays.asList(
            // Articles
            "a", "an", "the",
            // Prepositions (some are kept in ASL)
            "for", "of", "at", "by", "with", "from", "to",
            // Helping verbs
            "is", "am", "are", "was", "were", "be", "been", "being",
            "have", "has", "had", "do", "does", "did",
            // Modal verbs (some are kept)
            "will", "shall", "may", "might", "must", "can", "could", "should", "would",
            // Conjunctions (some are kept)
            "and", "but", "or", "nor", "for", "so", "yet",
            // Fillers
            "um", "uh", "ah", "like", "you know", "i mean"
    ));

    // Words to keep in ASL (important pronouns, verbs, nouns)
    private static final Set<String> KEEP_WORDS = new HashSet<>(Arrays.asList(
            // Pronouns (important in ASL)
            "i", "you", "he", "she", "it", "we", "they", "me", "him", "her", "us", "them",
            "my", "your", "his", "her", "its", "our", "their", "mine", "yours", "hers", "ours", "theirs",
            "myself", "yourself", "himself", "herself", "itself", "ourselves", "yourselves", "themselves",

            // Important verbs
            "want", "need", "like", "love", "hate", "go", "come", "see", "look", "watch",
            "hear", "listen", "speak", "talk", "say", "tell", "ask", "answer", "know", "understand",
            "think", "feel", "believe", "hope", "wish", "help", "give", "take", "bring", "get",
            "make", "do", "work", "play", "eat", "drink", "sleep", "wake", "live", "die",

            // Important nouns (time, people, places)
            "time", "day", "night", "morning", "afternoon", "evening", "today", "tomorrow", "yesterday",
            "week", "month", "year", "now", "then", "before", "after", "soon", "later",
            "person", "people", "man", "woman", "child", "boy", "girl", "baby",
            "place", "home", "house", "school", "work", "store", "hospital", "doctor",

            // Important question words
            "what", "where", "when", "why", "how", "who", "which", "whose"
    ));

    /**
     * Convert English sentence to ASL Gloss
     * @param englishText Input English text
     * @return ASL Gloss formatted string
     */
    public String convertToASLGloss(String englishText) {
        if (englishText == null || englishText.trim().isEmpty()) {
            return "";
        }

        System.out.println("ASLGlossConverter: Converting English: \"" + englishText + "\"");

        // Step 1: Tokenize and clean
        List<String> tokens = tokenizeAndClean(englishText);

        // Step 2: Apply ASL grammar rules (simplified: remove filler words)
        List<String> aslTokens = applyASLGrammar(tokens);

        // Step 3: Convert to uppercase (ASL Gloss convention)
        List<String> glossTokens = convertToGlossFormat(aslTokens);

        // Step 4: Join tokens with spaces
        String aslGloss = String.join(" ", glossTokens);

        System.out.println("ASLGlossConverter: ASL Gloss result: \"" + aslGloss + "\"");
        return aslGloss;
    }

    /**
     * Step 1: Tokenize and clean English text
     */
    private List<String> tokenizeAndClean(String text) {
        // Convert to lowercase and split
        String[] words = text.toLowerCase().split("\\s+");
        List<String> cleanedTokens = new ArrayList<>();

        for (String word : words) {
            // Remove punctuation
            word = word.replaceAll("[^a-zA-Z]", "");

            if (!word.isEmpty()) {
                cleanedTokens.add(word);
            }
        }

        return cleanedTokens;
    }

    /**
     * Step 2: Apply ASL grammar rules (simplified: remove filler words)
     */
    private List<String> applyASLGrammar(List<String> tokens) {
        if (tokens.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> aslTokens = new ArrayList<>();
        for (String token : tokens) {
            if (shouldKeepInASL(token)) {
                aslTokens.add(token);
            }
        }
        return aslTokens;
    }

    /**
     * Check if word should be kept in ASL
     */
    private boolean shouldKeepInASL(String word) {
        // Remove common English grammar words
        if (REMOVE_WORDS.contains(word)) {
            return false;
        }

        // Keep important words
        if (KEEP_WORDS.contains(word)) {
            return true;
        }

        // Keep words that are likely important (nouns, adjectives)
        return word.length() > 2; // Keep longer words, likely content words
    }

    /**
     * Step 3: Convert to ASL Gloss format (uppercase)
     */
    private List<String> convertToGlossFormat(List<String> tokens) {
        List<String> glossTokens = new ArrayList<>();
        for (String token : tokens) {
            glossTokens.add(token.toUpperCase());
        }
        return glossTokens;
    }

    /**
     * Get example conversions for debugging
     */
    public String getExampleConversions() {
        StringBuilder examples = new StringBuilder();
        examples.append("ASL Gloss Conversion Examples:\n");

        String[][] testCases = {
                {"Thank you for your help", "THANK YOU YOUR HELP"},
                {"I need water and food", "I NEED WATER FOOD"},
                {"Where is the bathroom", "WHERE BATHROOM"},
                {"What time is it now", "WHAT TIME NOW"},
                {"My name is Alex", "MY NAME ALEX"},
                {"How are you today", "HOW YOU TODAY"},
                {"I want to go to bathroom", "I WANT GO BATHROOM"},
                {"Please help me find the doctor", "PLEASE HELP ME FIND DOCTOR"},
                {"Can you tell me the time", "YOU TELL ME TIME"},
                {"I like to play with my friends", "I LIKE PLAY MY FRIENDS"}
        };

        for (String[] testCase : testCases) {
            String english = testCase[0];
            String expected = testCase[1];
            String actual = convertToASLGloss(english);

            examples.append("English: \"").append(english).append("\"\n");
            examples.append("Expected: ").append(expected).append("\n");
            examples.append("Actual: ").append(actual).append("\n");
            examples.append("Match: ").append(expected.equals(actual) ? "✓" : "✗").append("\n\n");
        }

        return examples.toString();
    }

    /**
     * Get statistics about the conversion
     */
    public String getConversionStats(String englishText) {
        String aslGloss = convertToASLGloss(englishText);

        String[] englishWords = englishText.split("\\s+");
        String[] aslWords = aslGloss.split("\\s+");

        return String.format(
                "Conversion Stats:\n" +
                        "English words: %d\n" +
                        "ASL Gloss words: %d\n" +
                        "Reduction: %.1f%%\n" +
                        "English: %s\n" +
                        "ASL Gloss: %s",
                englishWords.length,
                aslWords.length,
                (1 - (double)aslWords.length / englishWords.length) * 100,
                englishText,
                aslGloss
        );
    }
}
