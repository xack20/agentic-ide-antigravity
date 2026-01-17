package com.usermanagement.application.dto.command;

/**
 * Command DTO for assigning a role to a user.
 */
public record AssignRoleCommand(
        String targetUserId,
        String roleId,
        String actorUserId) {
}
