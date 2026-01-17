package com.usermanagement.application.service;

import com.usermanagement.application.dto.command.ChangePasswordCommand;
import com.usermanagement.application.port.output.*;
import com.usermanagement.application.validation.PasswordPolicyValidator;
import com.usermanagement.domain.entity.AuditLog;
import com.usermanagement.domain.entity.User;
import com.usermanagement.domain.enums.UserStatus;
import com.usermanagement.domain.exception.*;
import com.usermanagement.domain.valueobject.HashedPassword;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Use case service for changing passwords.
 * Implements Story 4: Change Password
 */
@Service
public class ChangePasswordService {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final AuditLogRepository auditLogRepository;
    private final PasswordHasher passwordHasher;
    private final PasswordPolicyValidator passwordValidator;
    private final int loginAttemptLimit;
    private final int passwordHistoryCount;
    private final boolean invalidateAllSessions;

    public ChangePasswordService(
            UserRepository userRepository,
            SessionRepository sessionRepository,
            AuditLogRepository auditLogRepository,
            PasswordHasher passwordHasher,
            PasswordPolicyValidator passwordValidator,
            @Value("${policy.rate-limit.login.attempts:5}") int loginAttemptLimit,
            @Value("${policy.password.history-count:5}") int passwordHistoryCount,
            @Value("${policy.password.invalidate-sessions-on-change:true}") boolean invalidateAllSessions) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.auditLogRepository = auditLogRepository;
        this.passwordHasher = passwordHasher;
        this.passwordValidator = passwordValidator;
        this.loginAttemptLimit = loginAttemptLimit;
        this.passwordHistoryCount = passwordHistoryCount;
        this.invalidateAllSessions = invalidateAllSessions;
    }

    /**
     * Changes a user's password.
     * 
     * AC1: Password change fails if current password is incorrect.
     * AC2: New password must pass policy + history checks.
     * AC3: On success, sessions are invalidated according to policy.
     * AC4: Password change is logged as a security event.
     *
     * @param command   The change password command
     * @param ipAddress Request IP for audit
     * @param userAgent Request user agent for audit
     */
    public void execute(ChangePasswordCommand command, String ipAddress, String userAgent) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new UserNotFoundException(command.userId()));

        // Check if account is deactivated
        if (user.getStatus() == UserStatus.DEACTIVATED) {
            throw new UnauthorizedException("Cannot change password for deactivated account");
        }

        // Check if account is locked
        if (user.isLocked(30)) {
            throw new AccountLockedException(user.getLoginAttempts().getMinutesRemaining());
        }

        // Verify current password (AC1)
        if (!passwordHasher.verify(command.currentPassword(), user.getPasswordHash().getHash())) {
            boolean locked = user.recordFailedLogin(loginAttemptLimit);
            userRepository.save(user);

            if (locked) {
                throw new AccountLockedException(30);
            }
            throw new InvalidCredentialsException();
        }

        // Validate new password against policy (AC2)
        passwordValidator.validate(
                command.newPassword(),
                user.getEmail().getValue(),
                user.getUsername(),
                user.getFullName().getValue());

        // Check password history (AC2)
        String newHash = passwordHasher.hash(command.newPassword());
        HashedPassword newHashedPassword = HashedPassword.fromHash(newHash);

        // Check if same as current password
        if (passwordHasher.verify(command.newPassword(), user.getPasswordHash().getHash())) {
            throw new WeakPasswordException("New password cannot be the same as current password");
        }

        // Check against password history
        for (HashedPassword historicPassword : user.getPasswordHistory()) {
            if (passwordHasher.verify(command.newPassword(), historicPassword.getHash())) {
                throw new WeakPasswordException("Password was used recently. Choose a different password.");
            }
        }

        // Change password
        user.changePassword(newHashedPassword, passwordHistoryCount);
        userRepository.save(user);

        // Invalidate sessions (AC3)
        if (invalidateAllSessions) {
            int revokedCount = sessionRepository.revokeAllByUserId(user.getId());
            // Could optionally keep current session - would need session ID parameter
        }

        // Audit log (AC4)
        AuditLog auditLog = AuditLog.create(
                "PASSWORD_CHANGED",
                command.userId(),
                command.userId(),
                "USER",
                null,
                Map.of("sessionsRevoked", invalidateAllSessions),
                null,
                ipAddress,
                userAgent,
                null);
        auditLogRepository.save(auditLog);
    }
}
