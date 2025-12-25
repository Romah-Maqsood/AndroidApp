package com.example.echosign.utils;

import android.content.Context;

public class SessionManager {

    public SessionManager(Context context) {
        // Empty constructor
    }

    public void createLoginSession(String username) {
        System.out.println("SessionManager - createLoginSession called with: " + username);
    }

    public boolean isLoggedIn() {
        System.out.println("SessionManager - isLoggedIn called, returning false");
        return false;
    }

    public String getUsername() {
        System.out.println("SessionManager - getUsername called, returning null");
        return null;
    }

    public void logoutUser() {
        System.out.println("SessionManager - logoutUser called");
    }
}