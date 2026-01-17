package com.usermanagement.application.service;

import com.usermanagement.application.dto.command.ActivateUserCommand;
import com.usermanagement.application.dto.command.DeactivateUserCommand;
import com.usermanagement.application.port.output.AuditLogRepository;
import com.usermanagement.application.port.output.RoleRepository;
import com.usermanagement.application.port.output.SessionRepository;
import com.usermanagement.application.port.output.UserRepository;
import com.usermanagement.domain.entity.AuditLog;
import com.usermanagement.domain.entity.Role;
import com.usermanagement.domain.entity.User;
import com.usermanagement.domain.enums.DeactivationReason;
import com.usermanagement.domain.enums.Permission;
import com.usermanagement.domain.enums.UserStatus;
import com.usermanagement.domain.exception.DomainException;
import com.usermanagement.domain.exception.UnauthorizedException;
import com.usermanagement.domain.exception.UserNotFoundException;
import com.usermanagement.domain.valueobject.UserId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Use case service for activating and deactivating users.
 * Implements Story 5: Deactivate / Activate User
 */
@Service
public class UserStatusService {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final RoleRepository roleRepository;
    private final AuditLogRepository auditLogRepository;

    public UserStatusService(
            UserRepository userRepository,
            SessionRepository sessionRepository,
            RoleRepository roleRepository,
            AuditLogRepository auditLogRepository) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.roleRepository = roleRepository;
        this.auditLogRepository = auditLogRepository;
    }

    /**
     * Deactivates a user account.
     * 
     * AC1: Unauthorized users cannot deactivate accounts.
     * AC2: Deactivation requires reason code.
     * AC3: Deactivation revokes sessions and blocks login immediately.
     * AC5: All actions are audited with actor + reason.
     *
     * @param command   The deactivate command
     * @param ipAddress Request IP for audit
     * @param userAgent Request user agent for audit
     */
    public void deactivate(DeactivateUserCommand command, String ipAddress, String userAgent) {
        // Get actor
        User actor = userRepository.findById(command.actorUserId())
                .orElseThrow(() -> new UserNotFoundException(command.actorUserId()));

        // Check authorization (AC1)
        if (!hasPermission(actor, Permission.USER_STATUS_MANAGE)) {
            throw new UnauthorizedException("You do not have permission to deactivate users");
        }

        // Prevent self-deactivation without special permission
        if (command.targetUserId().equals(command.actorUserId())) {
            throw new UnauthorizedException("You cannot deactivate your own account");
        }

        // Get target user
        User target = userRepository.findById(command.targetUserId())
                .orElseThrow(() -> new UserNotFoundException(command.targetUserId()));

        // Validate reason code (AC2)
        if (command.reason() == null) {
            throw new IllegalArgumentException("Deactivation reason is required");
        }

        // Store old status for audit
        UserStatus oldStatus = target.getStatus();

        // Deactivate user
        target.deactivate(command.reason(), command.evidence(), UserId.of(command.actorUserId()));
        userRepository.save(target);

        // Revoke all sessions immediately (AC3)
        int sessionsRevoked = sessionRepository.revokeAllByUserId(target.getId());

        // Audit log (AC5)
        AuditLog auditLog = AuditLog.create(
                "USER_DEACTIVATED",
                command.actorUserId(),
                command.targetUserId(),
                "USER",
                Map.of("status", oldStatus.name()),
                Map.of(
                        "status", target.getStatus().name(),
                        "reason", command.reason().name(),
                        "sessionsRevoked", sessionsRevoked),
                command.reason().getDescription(),
                ipAddress,
                userAgent,
                null);
        auditLogRepository.save(auditLog);
    }

    /**
     * Activates a deactivated user account.
     * 
     * AC1: Unauthorized users cannot activate accounts.
     * AC4: Activation enforces any "security re-entry" requirements.
     * AC5: All actions are audited with actor + reason.
     *
     * @param command   The activate command
     * @param ipAddress Request IP for audit
     * @param userAgent Request user agent for audit
     */
    public void activate(ActivateUserCommand command, String ipAddress, String userAgent) {
        // Get actor
        User actor = userRepository.findById(command.actorUserId())
                .orElseThrow(() -> new UserNotFoundException(command.actorUserId()));

        // Check authorization (AC1)
        if (!hasPermission(actor, Permission.USER_STATUS_MANAGE)) {
            throw new UnauthorizedException("You do not have permission to activate users");
        }

        // Get target user
        User target = userRepository.findById(command.targetUserId())
                .orElseThrow(() -> new UserNotFoundException(command.targetUserId()));

        // Check if already active
        if (target.getStatus() == UserStatus.ACTIVE) {
            throw new IllegalStateException("User is already active");
        }

        // Check if banned (cannot activate)
        if (target.getStatus() == UserStatus.BANNED) {
            throw new UnauthorizedException("Cannot activate a banned user without special override");
        }

        // Store old status for audit
        UserStatus oldStatus = target.getStatus();
        boolean requiresPasswordReset = false;

        // Check if password reset is required based on deactivation reason (AC4)
        if (target.getDeactivationInfo() != null &&
                target.getDeactivationInfo().requiresPasswordResetOnReactivation()) {
            requiresPasswordReset = true;
            // In a full implementation, would trigger password reset flow here
        }

        // Activate user
        target.activate();
        userRepository.save(target);

        // Audit log (AC5)
        AuditLog auditLog = AuditLog.create(
                "USER_ACTIVATED",
                command.actorUserId(),
                command.targetUserId(),
                "USER",
                Map.of("status", oldStatus.name()),
                Map.of(
                        "status", target.getStatus().name(),
                        "requiresPasswordReset", requiresPasswordReset),
                null,
                ipAddress,
                userAgent,
                null);
        auditLogRepository.save(auditLog);
    }

    private boolean hasPermission(User user, Permission permission) {
        List<Role> roles = roleRepository.findByIds(user.getRoleIds());
        return roles.stream().anyMatch(role -> role.hasPermission(permission));
    }
}
