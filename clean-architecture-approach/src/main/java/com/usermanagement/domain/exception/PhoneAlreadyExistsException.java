package com.usermanagement.domain.exception;

public class PhoneAlreadyExistsException extends DomainException {
    public PhoneAlreadyExistsException(String phone) {
        super("PHONE_ALREADY_EXISTS", "An account with this phone number already exists: " + maskPhone(phone));
    }

    @Override
    public int getHttpStatus() {
        return 409; // Conflict
    }

    private static String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) {
            return "***";
        }
        return phone.substring(0, 4) + "****" + phone.substring(phone.length() - 2);
    }
}
