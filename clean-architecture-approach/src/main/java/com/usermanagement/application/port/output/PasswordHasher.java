package com.usermanagement.application.port.output;

/**
 * Output port for password hashing operations.
 */
public interface PasswordHasher {

    /**
     * Hashes a plain text password.
     */
    String hash(String plainPassword);

    /**
     * Verifies a plain text password against a hash.
     */
    boolean verify(String plainPassword, String hash);
}
