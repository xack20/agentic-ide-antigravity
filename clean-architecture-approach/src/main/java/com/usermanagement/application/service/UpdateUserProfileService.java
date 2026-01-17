package com.usermanagement.application.service;

import com.usermanagement.application.dto.command.UpdateProfileCommand;
import com.usermanagement.application.dto.response.UserProfileResponse;
import com.usermanagement.application.port.output.AuditLogRepository;
import com.usermanagement.application.port.output.RateLimiter;
import com.usermanagement.application.port.output.UserRepository;
import com.usermanagement.domain.entity.AuditLog;
import com.usermanagement.domain.entity.User;
import com.usermanagement.domain.entity.UserProfile;
import com.usermanagement.domain.exception.ConcurrentModificationException;
import com.usermanagement.domain.exception.RateLimitExceededException;
import com.usermanagement.domain.exception.UnauthorizedException;
import com.usermanagement.domain.exception.UserNotFoundException;
import com.usermanagement.domain.valueobject.Address;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Use case service for updating user profiles.
 * Implements Story 3: Update User Profile
 */
@Service
public class UpdateUserProfileService {

    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;
    private final RateLimiter rateLimiter;

    public UpdateUserProfileService(
            UserRepository userRepository,
            AuditLogRepository auditLogRepository,
            RateLimiter rateLimiter) {
        this.userRepository = userRepository;
        this.auditLogRepository = auditLogRepository;
        this.rateLimiter = rateLimiter;
    }

    /**
     * Updates a user's profile.
     * 
     * AC1: User can update allowed fields; disallowed fields are rejected.
     * AC2: Changing phone/email triggers verification workflow (not implemented
     * here).
     * AC3: Update requests with stale version/etag are rejected with a conflict
     * error.
     * AC4: Validation rules are applied per field.
     * AC5: Update actions are audited.
     *
     * @param command     The update command
     * @param requesterId The user making the request
     * @param ipAddress   Request IP for audit
     * @param userAgent   Request user agent for audit
     */
    public UserProfileResponse execute(UpdateProfileCommand command, String requesterId,
            String ipAddress, String userAgent) {
        // Authorization check (AC1)
        if (!command.userId().equals(requesterId)) {
            throw new UnauthorizedException("You can only update your own profile");
        }

        // Rate limiting
        if (!rateLimiter.tryConsume(requesterId, "profile-update")) {
            throw new RateLimitExceededException("profile updates");
        }

        // Get user
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new UserNotFoundException(command.userId()));

        // Optimistic locking check (AC3)
        if (command.expectedVersion() != null &&
                !command.expectedVersion().equals(user.getVersion())) {
            throw new ConcurrentModificationException();
        }

        // Track changes for audit
        Map<String, Object> oldValues = new HashMap<>();
        Map<String, Object> newValues = new HashMap<>();

        UserProfile currentProfile = user.getProfile();
        UserProfile updatedProfile = currentProfile;

        // Update display name
        if (command.displayName() != null &&
                !Objects.equals(command.displayName(), currentProfile.getDisplayName())) {
            oldValues.put("displayName", currentProfile.getDisplayName());
            newValues.put("displayName", command.displayName());
            updatedProfile = updatedProfile.withDisplayName(command.displayName());
        }

        // Update avatar URL
        if (command.avatarUrl() != null &&
                !Objects.equals(command.avatarUrl(), currentProfile.getAvatarUrl())) {
            oldValues.put("avatarUrl", currentProfile.getAvatarUrl());
            newValues.put("avatarUrl", command.avatarUrl());
            updatedProfile = updatedProfile.withAvatarUrl(command.avatarUrl());
        }

        // Update address
        if (command.street() != null || command.city() != null ||
                command.postalCode() != null || command.country() != null) {
            Address newAddress = Address.of(
                    command.street(),
                    command.city(),
                    command.postalCode(),
                    command.country());
            Address oldAddress = currentProfile.getAddress();
            if (!Objects.equals(newAddress, oldAddress)) {
                oldValues.put("address", oldAddress != null ? oldAddress.getFormatted() : null);
                newValues.put("address", newAddress.getFormatted());
                updatedProfile = updatedProfile.withAddress(newAddress);
            }
        }

        // Apply profile changes
        if (!newValues.isEmpty()) {
            user.updateProfile(updatedProfile);
            userRepository.save(user);

            // Audit log (AC5)
            AuditLog auditLog = AuditLog.create(
                    "PROFILE_UPDATED",
                    requesterId,
                    command.userId(),
                    "USER",
                    oldValues,
                    newValues,
                    null,
                    ipAddress,
                    userAgent,
                    null);
            auditLogRepository.save(auditLog);
        }

        return UserProfileResponse.from(user, false);
    }
}
