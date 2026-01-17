package com.usermanagement.application.dto.command;

/**
 * Command DTO for user registration.
 */
public record RegisterUserCommand(
        String email,
        String phone,
        String password,
        String fullName,
        String termsVersion,
        String privacyVersion,
        boolean marketingConsent,
        String ipAddress,
        String userAgent,
        String deviceFingerprint,
        String channel) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String email;
        private String phone;
        private String password;
        private String fullName;
        private String termsVersion;
        private String privacyVersion;
        private boolean marketingConsent;
        private String ipAddress;
        private String userAgent;
        private String deviceFingerprint;
        private String channel = "API";

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public Builder termsVersion(String termsVersion) {
            this.termsVersion = termsVersion;
            return this;
        }

        public Builder privacyVersion(String privacyVersion) {
            this.privacyVersion = privacyVersion;
            return this;
        }

        public Builder marketingConsent(boolean consent) {
            this.marketingConsent = consent;
            return this;
        }

        public Builder ipAddress(String ip) {
            this.ipAddress = ip;
            return this;
        }

        public Builder userAgent(String ua) {
            this.userAgent = ua;
            return this;
        }

        public Builder deviceFingerprint(String fp) {
            this.deviceFingerprint = fp;
            return this;
        }

        public Builder channel(String ch) {
            this.channel = ch;
            return this;
        }

        public RegisterUserCommand build() {
            return new RegisterUserCommand(email, phone, password, fullName,
                    termsVersion, privacyVersion, marketingConsent,
                    ipAddress, userAgent, deviceFingerprint, channel);
        }
    }
}
