package com.usermanagement.domain.entity;

import com.usermanagement.domain.enums.DeactivationReason;
import com.usermanagement.domain.enums.UserStatus;
import com.usermanagement.domain.exception.InvalidStateTransitionException;
import com.usermanagement.domain.valueobject.*;

import java.time.Instant;
import java.util.*;

/**
 * User aggregate root entity.
 * Encapsulates all user-related business logic and invariants.
 */
public class User {
    private final UserId id;
    private Email email;
    private Phone phone;
    private String username;
    private HashedPassword passwordHash;
    private List<HashedPassword> passwordHistory;
    private FullName fullName;
    private UserProfile profile;
    private Set<String> roleIds;
    private UserStatus status;
    private boolean emailVerified;
    private boolean phoneVerified;
    private TermsAcceptance termsAcceptance;
    private DeactivationInfo deactivationInfo;
    private LoginAttemptInfo loginAttempts;
    private RegistrationMetadata registrationMetadata;
    private Instant lastLoginAt;
    private Instant createdAt;
    private Instant updatedAt;
    private Long version;

    // Private constructor - use factory methods
    private User(UserId id) {
        this.id = id;
        this.roleIds = new HashSet<>();
        this.passwordHistory = new ArrayList<>();
        this.version = 0L;
    }

    /**
     * Factory method to create a new user during registration.
     */
    public static User create(
            Email email,
            Phone phone,
            HashedPassword passwordHash,
            FullName fullName,
            TermsAcceptance termsAcceptance,
            RegistrationMetadata registrationMetadata,
            String defaultRoleId,
            boolean requireEmailVerification) {
        User user = new User(UserId.generate());
        user.email = email;
        user.phone = phone;
        user.passwordHash = passwordHash;
        user.passwordHistory.add(passwordHash);
        user.fullName = fullName;
        user.profile = UserProfile.empty();
        user.roleIds.add(defaultRoleId);
        user.status = requireEmailVerification ? UserStatus.PENDING_VERIFICATION : UserStatus.ACTIVE;
        user.emailVerified = !requireEmailVerification;
        user.phoneVerified = false;
        user.termsAcceptance = termsAcceptance;
        user.loginAttempts = LoginAttemptInfo.initial();
        user.registrationMetadata = registrationMetadata;
        user.createdAt = Instant.now();
        user.updatedAt = Instant.now();
        return user;
    }

    /**
     * Factory method to reconstruct user from persistence.
     */
    public static User reconstitute(
            UserId id,
            Email email,
            Phone phone,
            String username,
            HashedPassword passwordHash,
            List<HashedPassword> passwordHistory,
            FullName fullName,
            UserProfile profile,
            Set<String> roleIds,
            UserStatus status,
            boolean emailVerified,
            boolean phoneVerified,
            TermsAcceptance termsAcceptance,
            DeactivationInfo deactivationInfo,
            LoginAttemptInfo loginAttempts,
            RegistrationMetadata registrationMetadata,
            Instant lastLoginAt,
            Instant createdAt,
            Instant updatedAt,
            Long version) {
        User user = new User(id);
        user.email = email;
        user.phone = phone;
        user.username = username;
        user.passwordHash = passwordHash;
        user.passwordHistory = passwordHistory != null ? new ArrayList<>(passwordHistory) : new ArrayList<>();
        user.fullName = fullName;
        user.profile = profile;
        user.roleIds = roleIds != null ? new HashSet<>(roleIds) : new HashSet<>();
        user.status = status;
        user.emailVerified = emailVerified;
        user.phoneVerified = phoneVerified;
        user.termsAcceptance = termsAcceptance;
        user.deactivationInfo = deactivationInfo;
        user.loginAttempts = loginAttempts;
        user.registrationMetadata = registrationMetadata;
        user.lastLoginAt = lastLoginAt;
        user.createdAt = createdAt;
        user.updatedAt = updatedAt;
        user.version = version;
        return user;
    }

    // ==================== Business Methods ====================

    /**
     * Verifies the user's email, transitioning to ACTIVE if pending verification.
     */
    public void verifyEmail() {
        this.emailVerified = true;
        if (this.status == UserStatus.PENDING_VERIFICATION) {
            this.status = UserStatus.ACTIVE;
        }
        this.updatedAt = Instant.now();
    }

    /**
     * Records a successful login.
     */
    public void recordSuccessfulLogin() {
        this.lastLoginAt = Instant.now();
        this.loginAttempts = LoginAttemptInfo.initial();
        this.updatedAt = Instant.now();
    }

    /**
     * Records a failed login attempt.
     * 
     * @return true if account is now locked
     */
    public boolean recordFailedLogin(int maxAttempts) {
        this.loginAttempts = this.loginAttempts.incrementFailure();
        this.updatedAt = Instant.now();
        return this.loginAttempts.getCount() >= maxAttempts;
    }

    /**
     * Checks if the account is currently locked.
     */
    public boolean isLocked(int lockoutMinutes) {
        return loginAttempts.isLockedOut(lockoutMinutes);
    }

    /**
     * Changes the user's password.
     */
    public void changePassword(HashedPassword newPasswordHash, int historyCount) {
        // Add current password to history
        if (passwordHistory.size() >= historyCount) {
            passwordHistory.remove(0);
        }
        passwordHistory.add(passwordHash);

        this.passwordHash = newPasswordHash;
        this.updatedAt = Instant.now();
    }

    /**
     * Checks if a password hash exists in the history.
     */
    public boolean isPasswordInHistory(HashedPassword hash) {
        return passwordHistory.stream()
                .anyMatch(h -> h.getHash().equals(hash.getHash()));
    }

    /**
     * Deactivates the user account.
     */
    public void deactivate(DeactivationReason reason, String evidence, UserId deactivatedBy) {
        if (!status.canTransitionTo(UserStatus.DEACTIVATED)) {
            throw new InvalidStateTransitionException(status, UserStatus.DEACTIVATED);
        }
        this.status = UserStatus.DEACTIVATED;
        this.deactivationInfo = DeactivationInfo.of(reason, evidence, deactivatedBy);
        this.updatedAt = Instant.now();
    }

    /**
     * Activates a deactivated user account.
     */
    public void activate() {
        if (!status.canTransitionTo(UserStatus.ACTIVE)) {
            throw new InvalidStateTransitionException(status, UserStatus.ACTIVE);
        }
        this.status = UserStatus.ACTIVE;
        this.deactivationInfo = null;
        this.updatedAt = Instant.now();
    }

    /**
     * Bans the user permanently.
     */
    public void ban(DeactivationReason reason, String evidence, UserId bannedBy) {
        if (!status.canTransitionTo(UserStatus.BANNED)) {
            throw new InvalidStateTransitionException(status, UserStatus.BANNED);
        }
        this.status = UserStatus.BANNED;
        this.deactivationInfo = DeactivationInfo.of(reason, evidence, bannedBy);
        this.updatedAt = Instant.now();
    }

    /**
     * Assigns a role to the user.
     */
    public void assignRole(String roleId) {
        this.roleIds.add(roleId);
        this.updatedAt = Instant.now();
    }

    /**
     * Revokes a role from the user.
     */
    public void revokeRole(String roleId) {
        this.roleIds.remove(roleId);
        this.updatedAt = Instant.now();
    }

    /**
     * Updates user profile information.
     */
    public void updateProfile(UserProfile newProfile) {
        this.profile = newProfile;
        this.updatedAt = Instant.now();
    }

    /**
     * Updates the full name.
     */
    public void updateFullName(FullName newName) {
        this.fullName = newName;
        this.updatedAt = Instant.now();
    }

    /**
     * Checks if user can perform login.
     */
    public boolean canLogin() {
        return status.canLogin() && emailVerified;
    }

    /**
     * Checks if user is an admin (has admin role).
     */
    public boolean hasRole(String roleId) {
        return roleIds.contains(roleId);
    }

    // ==================== Getters ====================

    public UserId getId() {
        return id;
    }

    public Email getEmail() {
        return email;
    }

    public Phone getPhone() {
        return phone;
    }

    public String getUsername() {
        return username;
    }

    public HashedPassword getPasswordHash() {
        return passwordHash;
    }

    public List<HashedPassword> getPasswordHistory() {
        return Collections.unmodifiableList(passwordHistory);
    }

    public FullName getFullName() {
        return fullName;
    }

    public UserProfile getProfile() {
        return profile;
    }

    public Set<String> getRoleIds() {
        return Collections.unmodifiableSet(roleIds);
    }

    public UserStatus getStatus() {
        return status;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public boolean isPhoneVerified() {
        return phoneVerified;
    }

    public TermsAcceptance getTermsAcceptance() {
        return termsAcceptance;
    }

    public DeactivationInfo getDeactivationInfo() {
        return deactivationInfo;
    }

    public LoginAttemptInfo getLoginAttempts() {
        return loginAttempts;
    }

    public RegistrationMetadata getRegistrationMetadata() {
        return registrationMetadata;
    }

    public Instant getLastLoginAt() {
        return lastLoginAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Long getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
