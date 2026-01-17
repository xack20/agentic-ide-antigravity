package com.usermanagement.application.dto.response;

import com.usermanagement.domain.entity.User;
import com.usermanagement.domain.enums.UserStatus;

import java.time.Instant;
import java.util.Set;

/**
 * Response DTO for user registration.
 */
public record RegisterUserResponse(
        String userId,
        String email,
        String phone,
        String fullName,
        UserStatus status,
        boolean emailVerificationRequired,
        Instant createdAt) {
    public static RegisterUserResponse from(User user, boolean emailVerificationRequired) {
        return new RegisterUserResponse(
                user.getId().getValue(),
                user.getEmail().getMasked(),
                user.getPhone().getMasked(),
                user.getFullName().getValue(),
                user.getStatus(),
                emailVerificationRequired,
                user.getCreatedAt());
    }
}
