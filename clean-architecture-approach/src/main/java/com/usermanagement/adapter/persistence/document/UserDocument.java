package com.usermanagement.adapter.persistence.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * MongoDB document for User entity.
 */
@Document(collection = "users")
public class UserDocument {
    @Id
    private String id;

    @Indexed(unique = true)
    private String emailNormalized;
    private String emailOriginal;

    @Indexed(unique = true, sparse = true)
    private String phoneNormalized;
    private String phoneCountryCode;

    @Indexed(unique = true, sparse = true)
    private String username;

    private String passwordHash;
    private List<PasswordHistoryEntry> passwordHistory;

    private String fullName;
    private String fullNameNormalized;

    private ProfileDocument profile;

    private Set<String> roleIds;
    private String status;
    private boolean emailVerified;
    private boolean phoneVerified;

    private TermsAcceptanceDocument termsAcceptance;
    private DeactivationDocument deactivation;
    private LoginAttemptDocument loginAttempts;
    private RegistrationMetadataDocument registrationMetadata;

    private Instant lastLoginAt;
    private Instant createdAt;
    private Instant updatedAt;

    @Version
    private Long version;

    // Embedded documents
    public record PasswordHistoryEntry(String hash, Instant createdAt) {
    }

    public record ProfileDocument(
            String displayName,
            String avatarUrl,
            AddressDocument address,
            String dateOfBirth,
            PreferencesDocument preferences) {
    }

    public record AddressDocument(
            String street,
            String city,
            String postalCode,
            String country) {
    }

    public record PreferencesDocument(
            boolean emailNotifications,
            boolean smsNotifications,
            String language,
            String timezone,
            Map<String, Object> customSettings) {
    }

    public record TermsAcceptanceDocument(
            String termsVersion,
            String privacyVersion,
            Instant acceptedAt,
            boolean marketingConsent) {
    }

    public record DeactivationDocument(
            String reason,
            String evidence,
            String deactivatedBy,
            Instant deactivatedAt) {
    }

    public record LoginAttemptDocument(
            int count,
            Instant lastAttemptAt,
            Instant lockedUntil) {
    }

    public record RegistrationMetadataDocument(
            Instant registeredAt,
            String ipAddress,
            String userAgent,
            String deviceFingerprint,
            String channel) {
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmailNormalized() {
        return emailNormalized;
    }

    public void setEmailNormalized(String emailNormalized) {
        this.emailNormalized = emailNormalized;
    }

    public String getEmailOriginal() {
        return emailOriginal;
    }

    public void setEmailOriginal(String emailOriginal) {
        this.emailOriginal = emailOriginal;
    }

    public String getPhoneNormalized() {
        return phoneNormalized;
    }

    public void setPhoneNormalized(String phoneNormalized) {
        this.phoneNormalized = phoneNormalized;
    }

    public String getPhoneCountryCode() {
        return phoneCountryCode;
    }

    public void setPhoneCountryCode(String phoneCountryCode) {
        this.phoneCountryCode = phoneCountryCode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public List<PasswordHistoryEntry> getPasswordHistory() {
        return passwordHistory;
    }

    public void setPasswordHistory(List<PasswordHistoryEntry> passwordHistory) {
        this.passwordHistory = passwordHistory;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFullNameNormalized() {
        return fullNameNormalized;
    }

    public void setFullNameNormalized(String fullNameNormalized) {
        this.fullNameNormalized = fullNameNormalized;
    }

    public ProfileDocument getProfile() {
        return profile;
    }

    public void setProfile(ProfileDocument profile) {
        this.profile = profile;
    }

    public Set<String> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(Set<String> roleIds) {
        this.roleIds = roleIds;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public boolean isPhoneVerified() {
        return phoneVerified;
    }

    public void setPhoneVerified(boolean phoneVerified) {
        this.phoneVerified = phoneVerified;
    }

    public TermsAcceptanceDocument getTermsAcceptance() {
        return termsAcceptance;
    }

    public void setTermsAcceptance(TermsAcceptanceDocument termsAcceptance) {
        this.termsAcceptance = termsAcceptance;
    }

    public DeactivationDocument getDeactivation() {
        return deactivation;
    }

    public void setDeactivation(DeactivationDocument deactivation) {
        this.deactivation = deactivation;
    }

    public LoginAttemptDocument getLoginAttempts() {
        return loginAttempts;
    }

    public void setLoginAttempts(LoginAttemptDocument loginAttempts) {
        this.loginAttempts = loginAttempts;
    }

    public RegistrationMetadataDocument getRegistrationMetadata() {
        return registrationMetadata;
    }

    public void setRegistrationMetadata(RegistrationMetadataDocument registrationMetadata) {
        this.registrationMetadata = registrationMetadata;
    }

    public Instant getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(Instant lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
