package com.usermanagement.application.service;

import com.usermanagement.application.dto.response.UserProfileResponse;
import com.usermanagement.application.port.output.AuditLogRepository;
import com.usermanagement.application.port.output.RoleRepository;
import com.usermanagement.application.port.output.UserRepository;
import com.usermanagement.domain.entity.AuditLog;
import com.usermanagement.domain.entity.Role;
import com.usermanagement.domain.entity.User;
import com.usermanagement.domain.enums.Permission;
import com.usermanagement.domain.exception.UnauthorizedException;
import com.usermanagement.domain.exception.UserNotFoundException;
import com.usermanagement.domain.valueobject.UserId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Use case service for viewing user profiles.
 * Implements Story 2: View User Profile
 */
@Service
public class ViewUserProfileService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuditLogRepository auditLogRepository;

    public ViewUserProfileService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            AuditLogRepository auditLogRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.auditLogRepository = auditLogRepository;
    }

    /**
     * Views a user profile.
     * 
     * AC1: User can fetch their own profile and sees only allowed fields.
     * AC2: User cannot view another user's profile unless they have admin
     * permissions.
     * AC3: Response excludes all secrets and internal-only fields.
     * AC4: Masking rules apply to sensitive fields based on policy.
     * AC5: Admin profile access is logged.
     *
     * @param targetUserId The user ID to view
     * @param requesterId  The user ID making the request
     * @param ipAddress    Request IP for audit
     * @param userAgent    Request user agent for audit
     */
    public UserProfileResponse execute(String targetUserId, String requesterId,
            String ipAddress, String userAgent) {
        // Get the target user
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new UserNotFoundException(targetUserId));

        boolean isSelf = targetUserId.equals(requesterId);
        boolean isAdmin = false;
        boolean maskSensitive = true;

        if (!isSelf) {
            // Check if requester has admin permissions (AC2)
            User requester = userRepository.findById(requesterId)
                    .orElseThrow(() -> new UserNotFoundException(requesterId));

            isAdmin = hasPermission(requester, Permission.USER_READ);

            if (!isAdmin) {
                throw new UnauthorizedException("You do not have permission to view this profile");
            }

            // Admin access is logged (AC5)
            AuditLog auditLog = AuditLog.create(
                    "PROFILE_VIEWED_BY_ADMIN",
                    requesterId,
                    targetUserId,
                    "USER",
                    null,
                    Map.of("viewedFields", "profile"),
                    null,
                    ipAddress,
                    userAgent,
                    null);
            auditLogRepository.save(auditLog);

            // Admins with elevated permission see unmasked data
            maskSensitive = !hasPermission(requester, Permission.USER_EXPORT);
        } else {
            // User viewing own profile sees unmasked data (AC1)
            maskSensitive = false;
        }

        // Build response with appropriate masking (AC3, AC4)
        return UserProfileResponse.from(targetUser, maskSensitive);
    }

    private boolean hasPermission(User user, Permission permission) {
        List<Role> roles = roleRepository.findByIds(user.getRoleIds());
        return roles.stream().anyMatch(role -> role.hasPermission(permission));
    }
}
