package com.usermanagement.application.dto.command;

/**
 * Command DTO for revoking a role from a user.
 */
public record RevokeRoleCommand(
        String targetUserId,
        String roleId,
        String actorUserId) {
}
