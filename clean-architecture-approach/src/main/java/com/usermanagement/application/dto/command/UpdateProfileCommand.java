package com.usermanagement.application.dto.command;

/**
 * Command DTO for updating user profile.
 */
public record UpdateProfileCommand(
        String userId,
        String displayName,
        String avatarUrl,
        String street,
        String city,
        String postalCode,
        String country,
        Long expectedVersion // For optimistic locking
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String userId;
        private String displayName;
        private String avatarUrl;
        private String street;
        private String city;
        private String postalCode;
        private String country;
        private Long expectedVersion;

        public Builder userId(String id) {
            this.userId = id;
            return this;
        }

        public Builder displayName(String name) {
            this.displayName = name;
            return this;
        }

        public Builder avatarUrl(String url) {
            this.avatarUrl = url;
            return this;
        }

        public Builder street(String s) {
            this.street = s;
            return this;
        }

        public Builder city(String c) {
            this.city = c;
            return this;
        }

        public Builder postalCode(String pc) {
            this.postalCode = pc;
            return this;
        }

        public Builder country(String c) {
            this.country = c;
            return this;
        }

        public Builder expectedVersion(Long v) {
            this.expectedVersion = v;
            return this;
        }

        public UpdateProfileCommand build() {
            return new UpdateProfileCommand(userId, displayName, avatarUrl,
                    street, city, postalCode, country, expectedVersion);
        }
    }
}
