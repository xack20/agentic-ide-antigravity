package com.usermanagement.domain.entity;

import com.usermanagement.domain.enums.DeactivationReason;
import com.usermanagement.domain.enums.UserStatus;
import com.usermanagement.domain.exception.InvalidStateTransitionException;
import com.usermanagement.domain.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = User.create(
                Email.of("test@example.com"),
                Phone.of("+8801712345678"),
                HashedPassword.fromHash("hashedpassword123"),
                FullName.of("Test User"),
                TermsAcceptance.of("1.0", "1.0", false),
                RegistrationMetadata.of("127.0.0.1", "TestAgent", null, "API"),
                "role-user-id",
                false // no email verification required
        );
    }

    @Test
    @DisplayName("Should create user with ACTIVE status when no verification required")
    void shouldCreateActiveUserWhenNoVerificationRequired() {
        assertEquals(UserStatus.ACTIVE, user.getStatus());
        assertTrue(user.isEmailVerified());
    }

    @Test
    @DisplayName("Should create user with PENDING_VERIFICATION when verification required")
    void shouldCreatePendingUserWhenVerificationRequired() {
        User pendingUser = User.create(
                Email.of("pending@example.com"),
                Phone.of("+8801712345679"),
                HashedPassword.fromHash("hashedpassword123"),
                FullName.of("Pending User"),
                TermsAcceptance.of("1.0", "1.0", false),
                RegistrationMetadata.of("127.0.0.1", "TestAgent", null, "API"),
                "role-user-id",
                true // verification required
        );

        assertEquals(UserStatus.PENDING_VERIFICATION, pendingUser.getStatus());
        assertFalse(pendingUser.isEmailVerified());
    }

    @Test
    @DisplayName("Should verify email and transition to ACTIVE")
    void shouldVerifyEmailAndTransitionToActive() {
        User pendingUser = User.create(
                Email.of("verify@example.com"),
                Phone.of("+8801712345670"),
                HashedPassword.fromHash("hashedpassword123"),
                FullName.of("Verify User"),
                TermsAcceptance.of("1.0", "1.0", false),
                RegistrationMetadata.of("127.0.0.1", "TestAgent", null, "API"),
                "role-user-id",
                true);

        pendingUser.verifyEmail();

        assertTrue(pendingUser.isEmailVerified());
        assertEquals(UserStatus.ACTIVE, pendingUser.getStatus());
    }

    @Test
    @DisplayName("Should deactivate user")
    void shouldDeactivateUser() {
        user.deactivate(DeactivationReason.USER_REQUESTED, null, UserId.of("admin-id"));

        assertEquals(UserStatus.DEACTIVATED, user.getStatus());
        assertNotNull(user.getDeactivationInfo());
        assertEquals(DeactivationReason.USER_REQUESTED, user.getDeactivationInfo().getReason());
    }

    @Test
    @DisplayName("Should activate deactivated user")
    void shouldActivateDeactivatedUser() {
        user.deactivate(DeactivationReason.USER_REQUESTED, null, UserId.of("admin-id"));
        user.activate();

        assertEquals(UserStatus.ACTIVE, user.getStatus());
        assertNull(user.getDeactivationInfo());
    }

    @Test
    @DisplayName("Should not allow activating already active user")
    void shouldNotAllowActivatingActiveUser() {
        assertThrows(InvalidStateTransitionException.class, () -> user.activate());
    }

    @Test
    @DisplayName("Should assign and revoke roles")
    void shouldAssignAndRevokeRoles() {
        user.assignRole("admin-role-id");

        assertTrue(user.hasRole("admin-role-id"));
        assertTrue(user.hasRole("role-user-id"));

        user.revokeRole("admin-role-id");

        assertFalse(user.hasRole("admin-role-id"));
    }

    @Test
    @DisplayName("Should record successful login")
    void shouldRecordSuccessfulLogin() {
        assertNull(user.getLastLoginAt());

        user.recordSuccessfulLogin();

        assertNotNull(user.getLastLoginAt());
    }

    @Test
    @DisplayName("Should track failed login attempts")
    void shouldTrackFailedLoginAttempts() {
        boolean locked = user.recordFailedLogin(5);

        assertFalse(locked);
        assertEquals(1, user.getLoginAttempts().getCount());
    }

    @Test
    @DisplayName("Should lock after max failed attempts")
    void shouldLockAfterMaxFailedAttempts() {
        for (int i = 0; i < 4; i++) {
            user.recordFailedLogin(5);
        }

        boolean locked = user.recordFailedLogin(5);

        assertTrue(locked);
        assertEquals(5, user.getLoginAttempts().getCount());
    }

    @Test
    @DisplayName("Should check if user can login")
    void shouldCheckIfUserCanLogin() {
        assertTrue(user.canLogin());

        user.deactivate(DeactivationReason.USER_REQUESTED, null, UserId.of("admin"));

        assertFalse(user.canLogin());
    }
}
