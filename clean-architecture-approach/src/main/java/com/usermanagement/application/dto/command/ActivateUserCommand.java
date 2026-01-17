package com.usermanagement.application.dto.command;

/**
 * Command DTO for activating a user.
 */
public record ActivateUserCommand(
        String targetUserId,
        String actorUserId) {
}
