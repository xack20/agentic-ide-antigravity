package com.usermanagement.domain.entity;

import com.usermanagement.domain.valueobject.Address;

/**
 * User profile information.
 */
public class UserProfile {
    private String displayName;
    private String avatarUrl;
    private Address address;
    private String dateOfBirth;
    private UserPreferences preferences;

    private UserProfile() {
    }

    public static UserProfile empty() {
        return new UserProfile();
    }

    public static UserProfile of(String displayName, String avatarUrl, Address address,
            String dateOfBirth, UserPreferences preferences) {
        UserProfile profile = new UserProfile();
        profile.displayName = displayName;
        profile.avatarUrl = avatarUrl;
        profile.address = address;
        profile.dateOfBirth = dateOfBirth;
        profile.preferences = preferences;
        return profile;
    }

    public UserProfile withDisplayName(String displayName) {
        UserProfile updated = copy();
        updated.displayName = displayName;
        return updated;
    }

    public UserProfile withAvatarUrl(String avatarUrl) {
        UserProfile updated = copy();
        updated.avatarUrl = avatarUrl;
        return updated;
    }

    public UserProfile withAddress(Address address) {
        UserProfile updated = copy();
        updated.address = address;
        return updated;
    }

    private UserProfile copy() {
        return UserProfile.of(displayName, avatarUrl, address, dateOfBirth, preferences);
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public Address getAddress() {
        return address;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public UserPreferences getPreferences() {
        return preferences;
    }
}
