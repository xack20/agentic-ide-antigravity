package com.usermanagement.application.validation;

import com.usermanagement.domain.exception.InvalidEmailException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Validates email addresses against policy requirements.
 */
@Component
public class EmailPolicyValidator {

    private final Set<String> blockedDomains;
    private final boolean checkMxRecord;

    public EmailPolicyValidator(
            @Value("${policy.email.blocked-domains:}") Set<String> blockedDomains,
            @Value("${policy.email.check-mx-record:false}") boolean checkMxRecord) {
        this.blockedDomains = blockedDomains != null ? blockedDomains : Set.of();
        this.checkMxRecord = checkMxRecord;
    }

    /**
     * Validates an email domain against blocked list.
     * 
     * @throws InvalidEmailException if domain is blocked
     */
    public void validateDomain(String domain) {
        if (blockedDomains.contains(domain.toLowerCase())) {
            throw new InvalidEmailException("Temporary or disposable email addresses are not allowed");
        }

        // MX record check would go here if enabled
        if (checkMxRecord) {
            // In production, implement DNS MX lookup
            // For now, this is a placeholder
        }
    }

    /**
     * Checks if the email domain is blocked.
     */
    public boolean isDomainBlocked(String domain) {
        return blockedDomains.contains(domain.toLowerCase());
    }
}
