package com.usermanagement.adapter.web.controller;

import com.usermanagement.adapter.web.request.ChangePasswordRequest;
import com.usermanagement.adapter.web.request.UpdateProfileRequest;
import com.usermanagement.application.dto.command.ChangePasswordCommand;
import com.usermanagement.application.dto.command.UpdateProfileCommand;
import com.usermanagement.application.dto.response.UserProfileResponse;
import com.usermanagement.application.service.ChangePasswordService;
import com.usermanagement.application.service.UpdateUserProfileService;
import com.usermanagement.application.service.ViewUserProfileService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for user profile operations.
 * Implements Stories 2, 3, 4: View/Update Profile, Change Password
 */
@RestController
@RequestMapping("/api/v1/users")
public class ProfileController {

    private final ViewUserProfileService viewProfileService;
    private final UpdateUserProfileService updateProfileService;
    private final ChangePasswordService changePasswordService;

    public ProfileController(
            ViewUserProfileService viewProfileService,
            UpdateUserProfileService updateProfileService,
            ChangePasswordService changePasswordService) {
        this.viewProfileService = viewProfileService;
        this.updateProfileService = updateProfileService;
        this.changePasswordService = changePasswordService;
    }

    /**
     * Gets the current user's profile.
     * GET /api/v1/users/me
     * Implements Story 2: View User Profile
     */
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile(
            @RequestHeader("X-User-Id") String userId, // In production, get from JWT
            HttpServletRequest httpRequest) {
        UserProfileResponse response = viewProfileService.execute(
                userId,
                userId,
                getClientIp(httpRequest),
                httpRequest.getHeader("User-Agent"));
        return ResponseEntity.ok(response);
    }

    /**
     * Updates the current user's profile.
     * PATCH /api/v1/users/me
     * Implements Story 3: Update User Profile
     */
    @PatchMapping("/me")
    public ResponseEntity<UserProfileResponse> updateMyProfile(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody UpdateProfileRequest request,
            HttpServletRequest httpRequest) {
        UpdateProfileCommand command = UpdateProfileCommand.builder()
                .userId(userId)
                .displayName(request.displayName())
                .avatarUrl(request.avatarUrl())
                .street(request.street())
                .city(request.city())
                .postalCode(request.postalCode())
                .country(request.country())
                .expectedVersion(request.expectedVersion())
                .build();

        UserProfileResponse response = updateProfileService.execute(
                command,
                userId,
                getClientIp(httpRequest),
                httpRequest.getHeader("User-Agent"));
        return ResponseEntity.ok(response);
    }

    /**
     * Changes the current user's password.
     * PUT /api/v1/users/me/password
     * Implements Story 4: Change Password
     */
    @PutMapping("/me/password")
    public ResponseEntity<Void> changePassword(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody ChangePasswordRequest request,
            HttpServletRequest httpRequest) {
        ChangePasswordCommand command = new ChangePasswordCommand(
                userId,
                request.currentPassword(),
                request.newPassword());

        changePasswordService.execute(
                command,
                getClientIp(httpRequest),
                httpRequest.getHeader("User-Agent"));
        return ResponseEntity.noContent().build();
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
