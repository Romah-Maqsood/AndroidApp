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

    // Constructor
    public SessionManager(Context context) {
        this.context = context;
        // Create SharedPreferences with private mode (only this app can access)
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    /**
     * Create login session and save user credentials
     * @param username Username entered by user
     * @param password Password entered by user (stored for demo only)
     */
    public void createLoginSession(String username, String password) {
        // Step 6.2: Save login status and user data
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_PASSWORD, password); // Note: In real apps, NEVER store passwords like this

        // Commit changes
        editor.apply();

        // Log for debugging
        System.out.println("SessionManager: Login session created for user: " + username);
    }

    /**
     * Check if user is logged in
     * @return true if user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Get stored username
     * @return Username or null if not found
     */
    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, null);
    }

    /**
     * Get stored password (for demo only)
     * @return Password or null if not found
     */
    public String getPassword() {
        return sharedPreferences.getString(KEY_PASSWORD, null);
    }

    /**
     * Clear session data (logout)
     */
    public void logoutUser() {
        // Clear all stored data
        editor.clear();
        editor.apply();

        System.out.println("SessionManager: User logged out, session cleared");
    }

    /**
     * Get all stored data for debugging
     * @return String with all stored data
     */
    public String getAllData() {
        boolean isLoggedIn = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
        String username = sharedPreferences.getString(KEY_USERNAME, "null");
        String password = sharedPreferences.getString(KEY_PASSWORD, "null");

        return "Login Status: " + isLoggedIn +
                "\nUsername: " + username +
                "\nPassword: " + password;
    }
}