package com.usermanagement.infrastructure.service;

import com.usermanagement.application.port.output.PasswordHasher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * BCrypt implementation of PasswordHasher.
 */
@Service
public class BcryptPasswordHasher implements PasswordHasher {

    private final BCryptPasswordEncoder encoder;

    public BcryptPasswordHasher() {
        this.encoder = new BCryptPasswordEncoder(12); // Strength 12
    }

    @Override
    public String hash(String plainPassword) {
        return encoder.encode(plainPassword);
    }

    @Override
    public boolean verify(String plainPassword, String hash) {
        return encoder.matches(plainPassword, hash);
    }
}
