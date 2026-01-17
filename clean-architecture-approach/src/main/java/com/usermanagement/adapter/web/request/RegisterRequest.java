package com.usermanagement.adapter.web.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for user registration.
 */
public record RegisterRequest(
        @NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email,

        @NotBlank(message = "Phone is required") String phone,

        @NotBlank(message = "Password is required") @Size(min = 12, message = "Password must be at least 12 characters") String password,

        @NotBlank(message = "Full name is required") @Size(min = 2, max = 80, message = "Full name must be 2-80 characters") String fullName,

        @NotBlank(message = "Terms version is required") String termsVersion,

        @NotBlank(message = "Privacy policy version is required") String privacyVersion,

        @NotNull(message = "Marketing consent must be specified") Boolean marketingConsent) {
}
