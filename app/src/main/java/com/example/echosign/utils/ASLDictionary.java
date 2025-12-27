package com.example.echosign.utils;

import android.content.Context;
import android.content.res.Resources;
import java.util.HashMap;
import java.util.Map;

/**
 * Step 12: Core ASL Dictionary with real sign descriptions
 * Maps ASL words to actual sign descriptions (and later to GIFs)
 */
public class ASLDictionary {

    // Core ASL vocabulary (limited but meaningful)
    private Map<String, ASLSign> signDictionary;
    private Context context;

    // ASL Sign data class
    public static class ASLSign {
        private String word;
        private String description;
        private String gifResource; // For future GIF integration
        private String category;
        private String usageExample;

        public ASLSign(String word, String description, String category, String usageExample) {
            this.word = word;
            this.description = description;
            this.gifResource = "asl_" + word.toLowerCase(); // Future: asl_hello.gif
            this.category = category;
            this.usageExample = usageExample;
        }

        public String getWord() { return word; }
        public String getDescription() { return description; }
        public String getGifResource() { return gifResource; }
        public String getCategory() { return category; }
        public String getUsageExample() { return usageExample; }

        @Override
        public String toString() {
            return word.toUpperCase() + ": " + description;
        }
    }

    public ASLDictionary(Context context) {
        this.context = context;
        initializeCoreDictionary();
        System.out.println("ASLDictionary: Core ASL dictionary initialized with " + signDictionary.size() + " signs");
    }

    /**
     * Initialize core ASL dictionary with real sign descriptions
     */
    private void initializeCoreDictionary() {
        signDictionary = new HashMap<>();

        // ========== GREETINGS ==========
        addSign("HELLO",
                "Right hand flat, fingers together, thumb up. Move hand from forehead outward in a small arc.",
                "Greetings",
                "Use when meeting someone");

        addSign("HI",
                "Similar to HELLO but with smaller movement. Hand starts near temple.",
                "Greetings",
                "Casual greeting");

        addSign("THANK YOU",
                "Right hand flat, fingers together. Touch fingers to chin then move forward and downward.",
                "Politeness",
                "Express gratitude");

        addSign("PLEASE",
                "Open right hand, palm facing chest. Make circular motion on chest.",
                "Politeness",
                "Making a request politely");

        addSign("SORRY",
                "Make 'A' handshape (fist with thumb up). Rub in circular motion on chest.",
                "Politeness",
                "Apologizing for something");

        addSign("WELCOME",
                "Both hands open, palms up. Move hands toward body in inviting motion.",
                "Greetings",
                "Welcoming someone");

        // ========== ESSENTIALS ==========
        addSign("YES",
                "Make 'S' handshape (fist). Move up and down like nodding head.",
                "Responses",
                "Affirmative response");

        addSign("NO",
                "First two fingers extended (like peace sign), thumb over other fingers. Snap fingers together.",
                "Responses",
                "Negative response");

        addSign("OK",
                "Make 'O' with thumb and index finger, other fingers extended. Move hand slightly.",
                "Responses",
                "Agreement or understanding");

        addSign("HELP",
                "Thumb-up hand on flat palm of other hand. Lift upward together.",
                "Needs",
                "Asking for assistance");

        addSign("NEED",
                "Bent index finger, other fingers extended. Pull toward chest twice.",
                "Needs",
                "Expressing requirement");

        addSign("WANT",
                "Both hands claw-shaped, palms up. Pull toward chest.",
                "Needs",
                "Expressing desire");

        // ========== BASIC NEEDS ==========
        addSign("WATER",
                "W handshape (index, middle, ring fingers up). Tap on chin.",
                "Needs",
                "Asking for water");

        addSign("FOOD",
                "Flat hand, fingers together. Touch fingers to mouth.",
                "Needs",
                "Asking for food");

        addSign("EAT",
                "Flat hand, bring to mouth as if putting food in.",
                "Actions",
                "To consume food");

        addSign("DRINK",
                "C handshape. Bring to mouth as if drinking.",
                "Actions",
                "To consume liquid");

        addSign("BATHROOM",
                "T handshape (thumb between index and middle). Shake side to side.",
                "Needs",
                "Need to use restroom");

        addSign("SLEEP",
                "Open hand in front of face, pull down as eyes closing.",
                "Actions",
                "Going to sleep");

        // ========== PEOPLE ==========
        addSign("I",
                "Point index finger to chest.",
                "People",
                "Referring to oneself");

        addSign("YOU",
                "Point index finger forward at person.",
                "People",
                "Referring to other person");

        addSign("ME",
                "Point to self with index finger.",
                "People",
                "Referring to self");

        addSign("MOTHER",
                "Thumb of open hand touches chin.",
                "Family",
                "Referring to mother");

        addSign("FATHER",
                "Thumb of open hand touches forehead.",
                "Family",
                "Referring to father");

        addSign("FRIEND",
                "Hook index fingers together, pull apart and back together.",
                "People",
                "Referring to friend");

        // ========== QUESTIONS ==========
        addSign("WHAT",
                "Both hands open, palms up. Shake slightly side to side.",
                "Questions",
                "Asking for information");

        addSign("WHERE",
                "Index finger extended, shake side to side.",
                "Questions",
                "Asking about location");

        addSign("WHEN",
                "Index finger extended, make small circles.",
                "Questions",
                "Asking about time");

        addSign("WHY",
                "Y handshape (thumb and pinky extended). Touch to forehead then pull away.",
                "Questions",
                "Asking for reason");

        addSign("HOW",
                "Both hands, palms up, fingers touching. Rotate upward.",
                "Questions",
                "Asking about manner");

        addSign("WHO",
                "Make 'L' shape with thumb and index. Circle near mouth.",
                "Questions",
                "Asking about person");

        // ========== TIME ==========
        addSign("NOW",
                "Both Y hands (thumb and pinky). Move downward quickly.",
                "Time",
                "At this moment");

        addSign("TODAY",
                "Y hand touches chin then moves down.",
                "Time",
                "This day");

        addSign("TOMORROW",
                "A handshape (thumb on side of chin). Move forward.",
                "Time",
                "Next day");

        addSign("YESTERDAY",
                "Y handshape, thumb touches cheek then moves back over shoulder.",
                "Time",
                "Previous day");

        addSign("TIME",
                "Index finger taps back of wrist where watch would be.",
                "Time",
                "Asking or telling time");

        // ========== EMOTIONS ==========
        addSign("HAPPY",
                "Both hands brush upward on chest twice with smiling expression.",
                "Emotions",
                "Feeling joy");

        addSign("SAD",
                "Both hands move downward in front of face with sad expression.",
                "Emotions",
                "Feeling sorrow");

        addSign("ANGRY",
                "Claw hands at chest, pulling outward with angry expression.",
                "Emotions",
                "Feeling anger");

        addSign("SCARED",
                "Both hands clawed, shake in front of chest with wide eyes.",
                "Emotions",
                "Feeling fear");

        addSign("LOVE",
                "Cross arms over chest, hands in fists.",
                "Emotions",
                "Feeling love");

        // ========== PLACES ==========
        addSign("HOME",
                "Flat hand touches cheek then moves to flat palm position.",
                "Places",
                "Referring to home");

        addSign("SCHOOL",
                "Clap hands together twice (like teacher getting attention).",
                "Places",
                "Referring to school");

        addSign("WORK",
                "S handshape (fist) pounds on back of other S hand.",
                "Places",
                "Referring to workplace");

        addSign("HOSPITAL",
                "H handshape (index and middle fingers extended) on wrist pulse.",
                "Places",
                "Referring to hospital");

        addSign("STORE",
                "Both hands, palms up. Move back and forth as if exchanging money.",
                "Places",
                "Referring to store");

        // ========== NUMBERS 1-10 ==========
        for (int i = 1; i <= 10; i++) {
            String signDescription = getNumberSignDescription(i);
            addSign(String.valueOf(i),
                    signDescription,
                    "Numbers",
                    "Number " + i);
        }
    }

    /**
     * Add a sign to the dictionary
     */
    private void addSign(String word, String description, String category, String usage) {
        ASLSign sign = new ASLSign(word, description, category, usage);
        signDictionary.put(word.toUpperCase(), sign);

        // Also add lowercase version for flexibility
        signDictionary.put(word.toLowerCase(), sign);
    }

    /**
     * Get description for number signs
     */
    private String getNumberSignDescription(int number) {
        switch (number) {
            case 1: return "Index finger extended upward";
            case 2: return "Index and middle fingers extended upward (peace sign)";
            case 3: return "Index, middle, and ring fingers extended upward";
            case 4: return "All fingers except thumb extended upward";
            case 5: return "All five fingers extended upward";
            case 6: return "Thumb and pinky extended (like phone gesture)";
            case 7: return "Thumb touches middle finger, other fingers extended";
            case 8: return "Thumb touches ring finger, other fingers extended";
            case 9: return "Thumb touches pinky, other fingers extended";
            case 10: return "Thumb up, shake side to side";
            default: return "Number " + number + " sign";
        }
    }

    /**
     * Check if word exists in ASL dictionary
     */
    public boolean hasSign(String word) {
        if (word == null) return false;
        String key = word.trim().toUpperCase();
        return signDictionary.containsKey(key) || signDictionary.containsKey(word.toLowerCase());
    }

    /**
     * Get ASL sign information
     */
    public ASLSign getSign(String word) {
        if (word == null) return null;

        String key = word.trim().toUpperCase();
        ASLSign sign = signDictionary.get(key);

        if (sign == null) {
            sign = signDictionary.get(word.toLowerCase());
        }

        return sign;
    }

    /**
     * Get sign description
     */
    public String getSignDescription(String word) {
        ASLSign sign = getSign(word);
        return sign != null ? sign.getDescription() : "No ASL sign found for: " + word;
    }

    /**
     * Get all signs in a category
     */
    public Map<String, ASLSign> getSignsByCategory(String category) {
        Map<String, ASLSign> categorySigns = new HashMap<>();

        for (Map.Entry<String, ASLSign> entry : signDictionary.entrySet()) {
            if (entry.getValue().getCategory().equalsIgnoreCase(category)) {
                categorySigns.put(entry.getKey(), entry.getValue());
            }
        }

        return categorySigns;
    }

    /**
     * Get all available categories
     */
    public String[] getCategories() {
        return new String[] {
                "Greetings", "Politeness", "Responses", "Needs", "Actions",
                "People", "Family", "Questions", "Time", "Emotions",
                "Places", "Numbers"
        };
    }

    /**
     * Get dictionary statistics
     */
    public String getDictionaryStats() {
        int totalSigns = signDictionary.size() / 2; // Divide by 2 because we store upper and lower case

        Map<String, Integer> categoryCounts = new HashMap<>();
        for (ASLSign sign : signDictionary.values()) {
            String category = sign.getCategory();
            categoryCounts.put(category, categoryCounts.getOrDefault(category, 0) + 1);
        }

        StringBuilder stats = new StringBuilder();
        stats.append("ASL Dictionary Statistics:\n");
        stats.append("Total unique signs: ").append(totalSigns).append("\n");
        stats.append("Categories:\n");

        for (Map.Entry<String, Integer> entry : categoryCounts.entrySet()) {
            stats.append("  ").append(entry.getKey()).append(": ").append(entry.getValue() / 2).append(" signs\n");
        }

        return stats.toString();
    }

    /**
     * Get example sentences using dictionary signs
     */
    public String[] getExampleSentences() {
        return new String[] {
                "HELLO HOW ARE YOU",
                "THANK YOU FOR YOUR HELP",
                "I NEED WATER PLEASE",
                "WHERE IS BATHROOM",
                "WHAT TIME IS IT NOW",
                "MY NAME IS [Fingerspell your name]",
                "I AM HAPPY TODAY",
                "DO YOU WANT FOOD",
                "I LOVE MY MOTHER FATHER",
                "SORRY I DON'T UNDERSTAND"
        };
    }

    /**
     * Get all signs for debugging
     */
    public String getAllSigns() {
        StringBuilder sb = new StringBuilder();
        sb.append("ASL Dictionary Contents:\n");

        // Use a set to avoid duplicates (upper/lower case)
        java.util.Set<String> displayed = new java.util.HashSet<>();

        for (Map.Entry<String, ASLSign> entry : signDictionary.entrySet()) {
            String word = entry.getKey().toUpperCase();

            if (!displayed.contains(word)) {
                ASLSign sign = entry.getValue();
                sb.append(word).append("\n");
                sb.append("  Description: ").append(sign.getDescription()).append("\n");
                sb.append("  Category: ").append(sign.getCategory()).append("\n");
                sb.append("  Usage: ").append(sign.getUsageExample()).append("\n");
                sb.append("  GIF: ").append(sign.getGifResource()).append(".gif").append("\n\n");

                displayed.add(word);
            }
        }

        return sb.toString();
    }
}