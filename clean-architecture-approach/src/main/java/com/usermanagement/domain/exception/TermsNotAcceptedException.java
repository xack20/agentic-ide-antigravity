package com.usermanagement.domain.exception;

public class TermsNotAcceptedException extends DomainException {
    public TermsNotAcceptedException() {
        super("TERMS_NOT_ACCEPTED", "You must accept the Terms of Service and Privacy Policy to register");
    }
}
