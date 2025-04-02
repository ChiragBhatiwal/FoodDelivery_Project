package com.example.vendorapp.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreference {

        private static SharedPreference instance;
        private final SharedPreferences userPrefs;
        private final SharedPreferences loginPrefs;

        private static final String USER_PREFS = "User";
        private static final String LOGIN_PREFS = "Login";

        // Private constructor to enforce Singleton pattern
        private SharedPreference(Context context) {
            Context appContext = context.getApplicationContext();
            userPrefs = appContext.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
            loginPrefs = appContext.getSharedPreferences(LOGIN_PREFS, Context.MODE_PRIVATE);
        }

        // Singleton instance getter
        public static synchronized SharedPreference getInstance(Context context) {
            if (instance == null) {
                instance = new SharedPreference(context);
            }
            return instance;
        }

        // Save to User Preferences
        public void saveUserInfo(String key, String value) {
            SharedPreferences.Editor editor = userPrefs.edit();
            editor.putString(key, value);
            editor.apply();
        }

        // Retrieve from User Preferences
        public String getUserInfo(String key, String defaultValue) {
            return userPrefs.getString(key, defaultValue);
        }

        // Save to Settings Preferences
        public void saveLoginInfo(String key, String value) {
            SharedPreferences.Editor editor = loginPrefs.edit();
            editor.putString(key, value);
            editor.apply();
        }

        // Retrieve from Settings Preferences
        public String getLoginInfo(String key, String defaultValue) {
            return loginPrefs.getString(key, defaultValue);
        }

        // Clear all data in User Preferences
        public void clearUserInfo() {
            SharedPreferences.Editor editor = userPrefs.edit();
            editor.clear();
            editor.apply();
        }

        // Clear all data in Settings Preferences
        public void clearLoginInfo() {
            SharedPreferences.Editor editor = loginPrefs.edit();
            editor.clear();
            editor.apply();
        }
}
