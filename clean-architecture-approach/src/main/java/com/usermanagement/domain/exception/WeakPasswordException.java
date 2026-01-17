package com.usermanagement.domain.exception;

import java.util.Collections;
import java.util.List;

/**
 * Thrown when password does not meet policy requirements.
 */
public class WeakPasswordException extends DomainException {
    private final List<String> violations;

    public WeakPasswordException(List<String> violations) {
        super("WEAK_PASSWORD", "Password does not meet security requirements");
        this.violations = violations != null ? violations : Collections.emptyList();
    }

    public WeakPasswordException(String violation) {
        this(List.of(violation));
    }

    public List<String> getViolations() {
        return violations;
    }
}
