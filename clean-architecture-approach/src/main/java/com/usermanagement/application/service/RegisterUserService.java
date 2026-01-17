package com.usermanagement.application.service;

import com.usermanagement.application.dto.command.RegisterUserCommand;
import com.usermanagement.application.dto.response.RegisterUserResponse;
import com.usermanagement.application.port.output.*;
import com.usermanagement.application.validation.EmailPolicyValidator;
import com.usermanagement.application.validation.PasswordPolicyValidator;
import com.usermanagement.domain.entity.AuditLog;
import com.usermanagement.domain.entity.RegistrationMetadata;
import com.usermanagement.domain.entity.Role;
import com.usermanagement.domain.entity.User;
import com.usermanagement.domain.exception.*;
import com.usermanagement.domain.valueobject.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Use case service for user registration.
 * Implements Story 1: Register User
 */
@Service
public class RegisterUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuditLogRepository auditLogRepository;
    private final PasswordHasher passwordHasher;
    private final RateLimiter rateLimiter;
    private final PasswordPolicyValidator passwordValidator;
    private final EmailPolicyValidator emailValidator;
    private final boolean requireEmailVerification;
    private final boolean normalizeGmail;

    public RegisterUserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            AuditLogRepository auditLogRepository,
            PasswordHasher passwordHasher,
            RateLimiter rateLimiter,
            PasswordPolicyValidator passwordValidator,
            EmailPolicyValidator emailValidator,
            @Value("${policy.require-email-verification:true}") boolean requireEmailVerification,
            @Value("${policy.email.normalize-gmail:true}") boolean normalizeGmail) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.auditLogRepository = auditLogRepository;
        this.passwordHasher = passwordHasher;
        this.rateLimiter = rateLimiter;
        this.passwordValidator = passwordValidator;
        this.emailValidator = emailValidator;
        this.requireEmailVerification = requireEmailVerification;
        this.normalizeGmail = normalizeGmail;
    }

    /**
     * Registers a new user.
     * 
     * AC1: Registration fails if email already exists (case-insensitive)
     * AC2: Registration fails if phone already exists after normalization
     * AC3: Password must satisfy policy
     * AC4: Terms and privacy policy acceptance is mandatory
     * AC5: Rate limiting enforced
     * AC6: User created with correct initial status and audit metadata
     */
    public RegisterUserResponse execute(RegisterUserCommand command) {
        // Rate limiting check
        String rateLimitKey = command.ipAddress() != null ? command.ipAddress() : "unknown";
        if (!rateLimiter.tryConsume(rateLimitKey, "registration")) {
            throw new RateLimitExceededException("registration");
        }

        // Validate terms acceptance (AC4)
        if (command.termsVersion() == null || command.termsVersion().isBlank() ||
                command.privacyVersion() == null || command.privacyVersion().isBlank()) {
            throw new TermsNotAcceptedException();
        }

        // Create and validate value objects
        Email email = Email.of(command.email(), normalizeGmail);

        // Validate email domain not blocked
        emailValidator.validateDomain(email.getDomain());

        // Check email uniqueness (AC1)
        if (userRepository.existsByEmail(email.getValue())) {
            throw new EmailAlreadyExistsException(command.email());
        }

        // Create and validate phone
        Phone phone = Phone.of(command.phone());

        // Check phone uniqueness (AC2)
        if (userRepository.existsByPhone(phone.getValue())) {
            throw new PhoneAlreadyExistsException(command.phone());
        }

        // Validate full name
        FullName fullName = FullName.of(command.fullName());

        // Validate password (AC3)
        passwordValidator.validate(command.password(), command.email(), null, command.fullName());

        // Hash password
        String hash = passwordHasher.hash(command.password());
        HashedPassword hashedPassword = HashedPassword.fromHash(hash);

        // Create terms acceptance
        TermsAcceptance termsAcceptance = TermsAcceptance.of(
                command.termsVersion(),
                command.privacyVersion(),
                command.marketingConsent());

        // Create registration metadata
        RegistrationMetadata metadata = RegistrationMetadata.of(
                command.ipAddress(),
                command.userAgent(),
                command.deviceFingerprint(),
                command.channel());

        // Get default role
        Role defaultRole = roleRepository.getDefaultRole();

        // Create user entity (AC6)
        User user = User.create(
                email,
                phone,
                hashedPassword,
                fullName,
                termsAcceptance,
                metadata,
                defaultRole.getId(),
                requireEmailVerification);

        // Save user
        User savedUser = userRepository.save(user);

        // Create audit log
        AuditLog auditLog = AuditLog.create(
                "USER_REGISTERED",
                null, // Self-registration has no actor
                savedUser.getId().getValue(),
                "USER",
                null,
                Map.of(
                        "email", email.getMasked(),
                        "phone", phone.getMasked(),
                        "status", savedUser.getStatus().name()),
                null,
                command.ipAddress(),
                command.userAgent(),
                null);
        auditLogRepository.save(auditLog);

        return RegisterUserResponse.from(savedUser, requireEmailVerification);
    }
}
