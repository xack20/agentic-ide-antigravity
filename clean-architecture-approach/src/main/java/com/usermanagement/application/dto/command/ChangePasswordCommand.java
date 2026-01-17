package com.usermanagement.application.dto.command;

/**
 * Command DTO for changing password.
 */
public record ChangePasswordCommand(
        String userId,
        String currentPassword,
        String newPassword) {
}
