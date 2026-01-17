package com.usermanagement.domain.entity;

import java.util.Map;

/**
 * User preferences (notification settings, UI preferences, etc.)
 */
public class UserPreferences {
    private boolean emailNotifications;
    private boolean smsNotifications;
    private String language;
    private String timezone;
    private Map<String, Object> customSettings;

    private UserPreferences() {
    }

    public static UserPreferences defaults() {
        UserPreferences prefs = new UserPreferences();
        prefs.emailNotifications = true;
        prefs.smsNotifications = false;
        prefs.language = "en";
        prefs.timezone = "UTC";
        return prefs;
    }

    public static UserPreferences of(boolean emailNotifications, boolean smsNotifications,
            String language, String timezone, Map<String, Object> customSettings) {
        UserPreferences prefs = new UserPreferences();
        prefs.emailNotifications = emailNotifications;
        prefs.smsNotifications = smsNotifications;
        prefs.language = language;
        prefs.timezone = timezone;
        prefs.customSettings = customSettings;
        return prefs;
    }

    public boolean isEmailNotifications() {
        return emailNotifications;
    }

    public boolean isSmsNotifications() {
        return smsNotifications;
    }

    public String getLanguage() {
        return language;
    }

    public String getTimezone() {
        return timezone;
    }

    public Map<String, Object> getCustomSettings() {
        return customSettings;
    }
}
