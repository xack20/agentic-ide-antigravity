package com.usermanagement.adapter.web.request;

/**
 * Request DTO for updating user profile.
 */
public record UpdateProfileRequest(
        String displayName,
        String avatarUrl,
        String street,
        String city,
        String postalCode,
        String country,
        Long expectedVersion) {
}
