package com.usermanagement.domain.valueobject;

import java.time.Instant;
import java.util.Objects;

/**
 * Value object representing terms and privacy policy acceptance.
 */
public final class TermsAcceptance {
    private final String termsVersion;
    private final String privacyVersion;
    private final Instant acceptedAt;
    private final boolean marketingConsent;

    private TermsAcceptance(String termsVersion, String privacyVersion,
            Instant acceptedAt, boolean marketingConsent) {
        this.termsVersion = termsVersion;
        this.privacyVersion = privacyVersion;
        this.acceptedAt = acceptedAt;
        this.marketingConsent = marketingConsent;
    }

    /**
     * Creates a new TermsAcceptance record.
     */
    public static TermsAcceptance of(String termsVersion, String privacyVersion,
            boolean marketingConsent) {
        if (termsVersion == null || termsVersion.isBlank()) {
            throw new IllegalArgumentException("Terms version is required");
        }
        if (privacyVersion == null || privacyVersion.isBlank()) {
            throw new IllegalArgumentException("Privacy policy version is required");
        }
        return new TermsAcceptance(termsVersion, privacyVersion, Instant.now(), marketingConsent);
    }

    /**
     * Creates from stored data.
     */
    public static TermsAcceptance fromStored(String termsVersion, String privacyVersion,
            Instant acceptedAt, boolean marketingConsent) {
        return new TermsAcceptance(termsVersion, privacyVersion, acceptedAt, marketingConsent);
    }

    public String getTermsVersion() {
        return termsVersion;
    }

    public String getPrivacyVersion() {
        return privacyVersion;
    }

    public Instant getAcceptedAt() {
        return acceptedAt;
    }

    public boolean hasMarketingConsent() {
        return marketingConsent;
    }

    /**
     * Creates a new TermsAcceptance with updated marketing consent.
     */
    public TermsAcceptance withMarketingConsent(boolean consent) {
        return new TermsAcceptance(termsVersion, privacyVersion, acceptedAt, consent);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        TermsAcceptance that = (TermsAcceptance) o;
        return marketingConsent == that.marketingConsent &&
                Objects.equals(termsVersion, that.termsVersion) &&
                Objects.equals(privacyVersion, that.privacyVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(termsVersion, privacyVersion, marketingConsent);
    }
}
