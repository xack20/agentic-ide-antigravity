package com.usermanagement.application.dto.command;

import com.usermanagement.domain.enums.DeactivationReason;

/**
 * Command DTO for deactivating a user.
 */
public record DeactivateUserCommand(
        String targetUserId,
        String actorUserId,
        DeactivationReason reason,
        String evidence) {
}
