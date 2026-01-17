package com.usermanagement.adapter.web.controller;

import com.usermanagement.adapter.web.request.RegisterRequest;
import com.usermanagement.application.dto.command.RegisterUserCommand;
import com.usermanagement.application.dto.response.RegisterUserResponse;
import com.usermanagement.application.service.RegisterUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication operations.
 * Implements Story 1: Register User
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final RegisterUserService registerUserService;

    public AuthController(RegisterUserService registerUserService) {
        this.registerUserService = registerUserService;
    }

    /**
     * Registers a new user.
     * POST /api/v1/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponse> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest httpRequest) {
        RegisterUserCommand command = RegisterUserCommand.builder()
                .email(request.email())
                .phone(request.phone())
                .password(request.password())
                .fullName(request.fullName())
                .termsVersion(request.termsVersion())
                .privacyVersion(request.privacyVersion())
                .marketingConsent(request.marketingConsent())
                .ipAddress(getClientIp(httpRequest))
                .userAgent(httpRequest.getHeader("User-Agent"))
                .deviceFingerprint(httpRequest.getHeader("X-Device-Fingerprint"))
                .channel("API")
                .build();

        RegisterUserResponse response = registerUserService.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
