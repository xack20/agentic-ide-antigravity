package com.usermanagement.adapter.persistence.mapper;

import com.usermanagement.adapter.persistence.document.UserDocument;
import com.usermanagement.domain.entity.*;
import com.usermanagement.domain.enums.DeactivationReason;
import com.usermanagement.domain.enums.UserStatus;
import com.usermanagement.domain.valueobject.*;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Maps between User domain entity and UserDocument MongoDB document.
 */
@Component
public class UserDocumentMapper {

    /**
     * Maps User entity to UserDocument.
     */
    public UserDocument toDocument(User user) {
        UserDocument doc = new UserDocument();
        doc.setId(user.getId().getValue());
        doc.setEmailNormalized(user.getEmail().getValue());
        doc.setEmailOriginal(user.getEmail().getOriginalValue());
        doc.setPhoneNormalized(user.getPhone().getValue());
        doc.setPhoneCountryCode(user.getPhone().getCountryCode());
        doc.setUsername(user.getUsername());
        doc.setPasswordHash(user.getPasswordHash().getHash());

        // Password history
        doc.setPasswordHistory(user.getPasswordHistory().stream()
                .map(h -> new UserDocument.PasswordHistoryEntry(h.getHash(), h.getCreatedAt()))
                .collect(Collectors.toList()));

        doc.setFullName(user.getFullName().getValue());
        doc.setFullNameNormalized(user.getFullName().getNormalized());

        // Profile
        UserProfile profile = user.getProfile();
        if (profile != null) {
            UserDocument.AddressDocument addressDoc = null;
            if (profile.getAddress() != null && !profile.getAddress().isEmpty()) {
                Address addr = profile.getAddress();
                addressDoc = new UserDocument.AddressDocument(
                        addr.getStreet(), addr.getCity(), addr.getPostalCode(), addr.getCountry());
            }

            UserDocument.PreferencesDocument prefsDoc = null;
            if (profile.getPreferences() != null) {
                UserPreferences prefs = profile.getPreferences();
                prefsDoc = new UserDocument.PreferencesDocument(
                        prefs.isEmailNotifications(),
                        prefs.isSmsNotifications(),
                        prefs.getLanguage(),
                        prefs.getTimezone(),
                        prefs.getCustomSettings());
            }

            doc.setProfile(new UserDocument.ProfileDocument(
                    profile.getDisplayName(),
                    profile.getAvatarUrl(),
                    addressDoc,
                    profile.getDateOfBirth(),
                    prefsDoc));
        }

        doc.setRoleIds(new HashSet<>(user.getRoleIds()));
        doc.setStatus(user.getStatus().name());
        doc.setEmailVerified(user.isEmailVerified());
        doc.setPhoneVerified(user.isPhoneVerified());

        // Terms acceptance
        TermsAcceptance terms = user.getTermsAcceptance();
        if (terms != null) {
            doc.setTermsAcceptance(new UserDocument.TermsAcceptanceDocument(
                    terms.getTermsVersion(),
                    terms.getPrivacyVersion(),
                    terms.getAcceptedAt(),
                    terms.hasMarketingConsent()));
        }

        // Deactivation info
        DeactivationInfo deact = user.getDeactivationInfo();
        if (deact != null) {
            doc.setDeactivation(new UserDocument.DeactivationDocument(
                    deact.getReason().name(),
                    deact.getEvidence(),
                    deact.getDeactivatedBy() != null ? deact.getDeactivatedBy().getValue() : null,
                    deact.getDeactivatedAt()));
        }

        // Login attempts
        LoginAttemptInfo login = user.getLoginAttempts();
        if (login != null) {
            doc.setLoginAttempts(new UserDocument.LoginAttemptDocument(
                    login.getCount(),
                    login.getLastAttemptAt(),
                    login.getLockedUntil()));
        }

        // Registration metadata
        RegistrationMetadata meta = user.getRegistrationMetadata();
        if (meta != null) {
            doc.setRegistrationMetadata(new UserDocument.RegistrationMetadataDocument(
                    meta.getRegisteredAt(),
                    meta.getIpAddress(),
                    meta.getUserAgent(),
                    meta.getDeviceFingerprint(),
                    meta.getChannel()));
        }

        doc.setLastLoginAt(user.getLastLoginAt());
        doc.setCreatedAt(user.getCreatedAt());
        doc.setUpdatedAt(user.getUpdatedAt());
        doc.setVersion(user.getVersion());

        return doc;
    }

    /**
     * Maps UserDocument to User entity.
     */
    public User toEntity(UserDocument doc) {
        // Reconstruct value objects
        Email email = Email.of(doc.getEmailOriginal(), true);
        Phone phone = Phone.of(doc.getPhoneNormalized());
        FullName fullName = FullName.of(doc.getFullName());
        HashedPassword passwordHash = HashedPassword.fromHash(doc.getPasswordHash());

        // Password history
        List<HashedPassword> passwordHistory = doc.getPasswordHistory() != null
                ? doc.getPasswordHistory().stream()
                        .map(h -> HashedPassword.fromHash(h.hash(), h.createdAt()))
                        .collect(Collectors.toList())
                : new ArrayList<>();

        // Profile
        UserProfile profile = null;
        if (doc.getProfile() != null) {
            UserDocument.ProfileDocument profileDoc = doc.getProfile();

            Address address = null;
            if (profileDoc.address() != null) {
                address = Address.of(
                        profileDoc.address().street(),
                        profileDoc.address().city(),
                        profileDoc.address().postalCode(),
                        profileDoc.address().country());
            }

            UserPreferences prefs = null;
            if (profileDoc.preferences() != null) {
                UserDocument.PreferencesDocument prefsDoc = profileDoc.preferences();
                prefs = UserPreferences.of(
                        prefsDoc.emailNotifications(),
                        prefsDoc.smsNotifications(),
                        prefsDoc.language(),
                        prefsDoc.timezone(),
                        prefsDoc.customSettings());
            }

            profile = UserProfile.of(
                    profileDoc.displayName(),
                    profileDoc.avatarUrl(),
                    address,
                    profileDoc.dateOfBirth(),
                    prefs);
        } else {
            profile = UserProfile.empty();
        }

        // Terms acceptance
        TermsAcceptance termsAcceptance = null;
        if (doc.getTermsAcceptance() != null) {
            UserDocument.TermsAcceptanceDocument termsDoc = doc.getTermsAcceptance();
            termsAcceptance = TermsAcceptance.fromStored(
                    termsDoc.termsVersion(),
                    termsDoc.privacyVersion(),
                    termsDoc.acceptedAt(),
                    termsDoc.marketingConsent());
        }

        // Deactivation info
        DeactivationInfo deactivationInfo = null;
        if (doc.getDeactivation() != null) {
            UserDocument.DeactivationDocument deactDoc = doc.getDeactivation();
            deactivationInfo = DeactivationInfo.reconstitute(
                    DeactivationReason.valueOf(deactDoc.reason()),
                    deactDoc.evidence(),
                    deactDoc.deactivatedBy() != null ? UserId.of(deactDoc.deactivatedBy()) : null,
                    deactDoc.deactivatedAt());
        }

        // Login attempts
        LoginAttemptInfo loginAttempts = LoginAttemptInfo.initial();
        if (doc.getLoginAttempts() != null) {
            UserDocument.LoginAttemptDocument loginDoc = doc.getLoginAttempts();
            loginAttempts = LoginAttemptInfo.reconstitute(
                    loginDoc.count(),
                    loginDoc.lastAttemptAt(),
                    loginDoc.lockedUntil());
        }

        // Registration metadata
        RegistrationMetadata registrationMetadata = null;
        if (doc.getRegistrationMetadata() != null) {
            UserDocument.RegistrationMetadataDocument metaDoc = doc.getRegistrationMetadata();
            registrationMetadata = RegistrationMetadata.reconstitute(
                    metaDoc.registeredAt(),
                    metaDoc.ipAddress(),
                    metaDoc.userAgent(),
                    metaDoc.deviceFingerprint(),
                    metaDoc.channel());
        }

        return User.reconstitute(
                UserId.of(doc.getId()),
                email,
                phone,
                doc.getUsername(),
                passwordHash,
                passwordHistory,
                fullName,
                profile,
                doc.getRoleIds() != null ? new HashSet<>(doc.getRoleIds()) : new HashSet<>(),
                UserStatus.valueOf(doc.getStatus()),
                doc.isEmailVerified(),
                doc.isPhoneVerified(),
                termsAcceptance,
                deactivationInfo,
                loginAttempts,
                registrationMetadata,
                doc.getLastLoginAt(),
                doc.getCreatedAt(),
                doc.getUpdatedAt(),
                doc.getVersion());
    }
}
