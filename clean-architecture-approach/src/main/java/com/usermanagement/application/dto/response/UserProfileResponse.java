package com.usermanagement.application.dto.response;

import com.usermanagement.domain.entity.User;
import com.usermanagement.domain.entity.UserProfile;
import com.usermanagement.domain.enums.UserStatus;

import java.time.Instant;
import java.util.Set;

/**
 * Response DTO for user profile view.
 * Contains masking logic based on viewer permissions.
 */
public record UserProfileResponse(
        String userId,
        String email,
        String phone,
        String fullName,
        String displayName,
        String avatarUrl,
        AddressResponse address,
        UserStatus status,
        boolean emailVerified,
        boolean phoneVerified,
        Set<String> roles,
        Instant createdAt,
        Instant lastLoginAt,
        Long version) {
    public static UserProfileResponse from(User user, boolean maskSensitive) {
        UserProfile profile = user.getProfile();
        return new UserProfileResponse(
                user.getId().getValue(),
                maskSensitive ? user.getEmail().getMasked() : user.getEmail().getOriginalValue(),
                maskSensitive ? user.getPhone().getMasked() : user.getPhone().getValue(),
                user.getFullName().getValue(),
                profile != null ? profile.getDisplayName() : null,
                profile != null ? profile.getAvatarUrl() : null,
                profile != null && profile.getAddress() != null
                        ? AddressResponse.from(profile.getAddress())
                        : null,
                user.getStatus(),
                user.isEmailVerified(),
                user.isPhoneVerified(),
                user.getRoleIds(),
                user.getCreatedAt(),
                user.getLastLoginAt(),
                user.getVersion());
    }

    public record AddressResponse(
            String street,
            String city,
            String postalCode,
            String country) {
        public static AddressResponse from(com.usermanagement.domain.valueobject.Address address) {
            if (address == null || address.isEmpty())
                return null;
            return new AddressResponse(
                    address.getStreet(),
                    address.getCity(),
                    address.getPostalCode(),
                    address.getCountry());
        }
    }
}
