package com.example.echosign.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    // Shared Preferences file name
    private static final String PREF_NAME = "EchoSignPref";

    // Shared Preferences keys
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_SETUP_COMPLETE = "setupComplete";
    private static final String KEY_SIGN_MODE = "signMode";
    private static final String KEY_CAPTIONS_ENABLED = "captionsEnabled";
    private static final String KEY_USAGE_PURPOSE = "usagePurpose";


    // Constructor
    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    /**
     * Create login session and save user credentials
     */
    public void createLoginSession(String username, String password) {
        // Save login status and user data
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_PASSWORD, password);

        // Commit changes
        editor.apply();

        System.out.println("SessionManager: Login session CREATED for: " + username);
        System.out.println("SessionManager: isLoggedIn = true");
    }

    /**
     * Check if user is logged in
     */
    public boolean isLoggedIn() {
        boolean status = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
        System.out.println("SessionManager: Checking login status = " + status);
        return status;
    }

    /**
     * Get stored username
     */
    public String getUsername() {
        String username = sharedPreferences.getString(KEY_USERNAME, null);
        System.out.println("SessionManager: Retrieved username = " + username);
        return username;
    }

    /**
     * Get stored password (for demo only)
     */
    public String getPassword() {
        return sharedPreferences.getString(KEY_PASSWORD, null);
    }

    /**
     * Save user preferences from the setup screen.
     */
    public void saveUserPreferences(String signMode, boolean captionsEnabled, String usagePurpose) {
        editor.putString(KEY_SIGN_MODE, signMode);
        editor.putBoolean(KEY_CAPTIONS_ENABLED, captionsEnabled);
        editor.putString(KEY_USAGE_PURPOSE, usagePurpose);
        editor.apply();
    }

    /**
     * Get the stored sign mode preference.
     */
    public String getSignMode() {
        return sharedPreferences.getString(KEY_SIGN_MODE, "ASL"); // Default to ASL
    }

    /**
     * Check if captions are enabled.
     */
    public boolean areCaptionsEnabled() {
        return sharedPreferences.getBoolean(KEY_CAPTIONS_ENABLED, true); // Default to true
    }

    /**
     * Get the stored usage purpose.
     */
    public String getUsagePurpose() {
        return sharedPreferences.getString(KEY_USAGE_PURPOSE, "Learning"); // Default to Learning
    }

    /**
     * Mark the one-time setup as complete.
     */
    public void setSetupComplete(boolean isComplete) {
        editor.putBoolean(KEY_SETUP_COMPLETE, isComplete);
        editor.apply();
    }

    /**
     * Check if the one-time setup has been completed.
     */
    public boolean isSetupComplete() {
        return sharedPreferences.getBoolean(KEY_SETUP_COMPLETE, false);
    }

    /**
     * Clear session data (logout)
     */
    public void logoutUser() {
        // Get username before clearing
        String username = getUsername();

        // Clear all stored data
        editor.clear();
        editor.apply();

        System.out.println("SessionManager: User LOGGED OUT: " + username);
        System.out.println("SessionManager: All session data cleared");
    }

    /**
     * Get all stored data for debugging
     */
    public String getAllData() {
        boolean isLoggedIn = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
        String username = sharedPreferences.getString(KEY_USERNAME, "null");
        String password = sharedPreferences.getString(KEY_PASSWORD, "null");
        boolean setupComplete = isSetupComplete();
        String signMode = getSignMode();
        boolean captionsEnabled = areCaptionsEnabled();
        String usagePurpose = getUsagePurpose();

        return "isLoggedIn: " + isLoggedIn +
                "\\nUsername: " + username +
                "\\nPassword: " + (password.length() + " characters") +
                "\\nSetup Complete: " + setupComplete +
                "\\nSign Mode: " + signMode +
                "\\nCaptions Enabled: " + captionsEnabled +
                "\\nUsage Purpose: " + usagePurpose;
    }
}