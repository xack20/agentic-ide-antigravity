package com.usermanagement.adapter.web.controller;

import com.usermanagement.adapter.web.request.AssignRoleRequest;
import com.usermanagement.adapter.web.request.DeactivateRequest;
import com.usermanagement.adapter.web.request.UserSearchRequest;
import com.usermanagement.application.dto.command.ActivateUserCommand;
import com.usermanagement.application.dto.command.AssignRoleCommand;
import com.usermanagement.application.dto.command.DeactivateUserCommand;
import com.usermanagement.application.dto.command.RevokeRoleCommand;
import com.usermanagement.application.dto.response.UserProfileResponse;
import com.usermanagement.application.port.output.UserSearchCriteria;
import com.usermanagement.application.service.*;
import com.usermanagement.domain.entity.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for admin user management operations.
 * Implements Stories 5, 6, 7: Deactivate/Activate, Role Assignment, Search
 * Users
 */
@RestController
@RequestMapping("/api/v1/admin/users")
public class AdminUserController {

    private final ViewUserProfileService viewProfileService;
    private final UserStatusService userStatusService;
    private final RoleAssignmentService roleAssignmentService;
    private final UserSearchService userSearchService;

    public AdminUserController(
            ViewUserProfileService viewProfileService,
            UserStatusService userStatusService,
            RoleAssignmentService roleAssignmentService,
            UserSearchService userSearchService) {
        this.viewProfileService = viewProfileService;
        this.userStatusService = userStatusService;
        this.roleAssignmentService = roleAssignmentService;
        this.userSearchService = userSearchService;
    }

    /**
     * Searches/lists users.
     * GET /api/v1/admin/users
     * Implements Story 7: Search / List Users
     */
    @GetMapping
    public ResponseEntity<UserSearchService.SearchResult> searchUsers(
            @RequestHeader("X-User-Id") String adminId,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String roleId,
            @RequestParam(required = false) Boolean emailVerified,
            @RequestParam(defaultValue = "createdAt") String sortField,
            @RequestParam(defaultValue = "DESC") String sortDirection,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        UserSearchCriteria criteria = UserSearchCriteria.builder()
                .searchQuery(query)
                .emailVerified(emailVerified)
                .sortField(sortField)
                .sortDirection(sortDirection.equalsIgnoreCase("ASC")
                        ? UserSearchCriteria.SortDirection.ASC
                        : UserSearchCriteria.SortDirection.DESC)
                .page(page)
                .pageSize(pageSize)
                .build();

        UserSearchService.SearchResult result = userSearchService.execute(criteria, adminId);
        return ResponseEntity.ok(result);
    }

    /**
     * Gets a specific user's details.
     * GET /api/v1/admin/users/{id}
     * Implements Story 2: View User Profile (admin version)
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponse> getUserDetails(
            @PathVariable String id,
            @RequestHeader("X-User-Id") String adminId,
            HttpServletRequest httpRequest) {
        UserProfileResponse response = viewProfileService.execute(
                id,
                adminId,
                getClientIp(httpRequest),
                httpRequest.getHeader("User-Agent"));
        return ResponseEntity.ok(response);
    }

    /**
     * Deactivates a user.
     * POST /api/v1/admin/users/{id}/deactivate
     * Implements Story 5: Deactivate User
     */
    @PostMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateUser(
            @PathVariable String id,
            @RequestHeader("X-User-Id") String adminId,
            @Valid @RequestBody DeactivateRequest request,
            HttpServletRequest httpRequest) {
        DeactivateUserCommand command = new DeactivateUserCommand(
                id,
                adminId,
                request.reason(),
                request.evidence());

        userStatusService.deactivate(
                command,
                getClientIp(httpRequest),
                httpRequest.getHeader("User-Agent"));
        return ResponseEntity.noContent().build();
    }

    /**
     * Activates a user.
     * POST /api/v1/admin/users/{id}/activate
     * Implements Story 5: Activate User
     */
    @PostMapping("/{id}/activate")
    public ResponseEntity<Void> activateUser(
            @PathVariable String id,
            @RequestHeader("X-User-Id") String adminId,
            HttpServletRequest httpRequest) {
        ActivateUserCommand command = new ActivateUserCommand(id, adminId);

        userStatusService.activate(
                command,
                getClientIp(httpRequest),
                httpRequest.getHeader("User-Agent"));
        return ResponseEntity.noContent().build();
    }

    /**
     * Assigns a role to a user.
     * POST /api/v1/admin/users/{id}/roles
     * Implements Story 6: Role Assignment
     */
    @PostMapping("/{id}/roles")
    public ResponseEntity<Void> assignRole(
            @PathVariable String id,
            @RequestHeader("X-User-Id") String adminId,
            @Valid @RequestBody AssignRoleRequest request,
            HttpServletRequest httpRequest) {
        AssignRoleCommand command = new AssignRoleCommand(id, request.roleId(), adminId);

        roleAssignmentService.assignRole(
                command,
                getClientIp(httpRequest),
                httpRequest.getHeader("User-Agent"));
        return ResponseEntity.noContent().build();
    }

    /**
     * Revokes a role from a user.
     * DELETE /api/v1/admin/users/{id}/roles/{roleId}
     * Implements Story 6: Role Assignment
     */
    @DeleteMapping("/{id}/roles/{roleId}")
    public ResponseEntity<Void> revokeRole(
            @PathVariable String id,
            @PathVariable String roleId,
            @RequestHeader("X-User-Id") String adminId,
            HttpServletRequest httpRequest) {
        RevokeRoleCommand command = new RevokeRoleCommand(id, roleId, adminId);

        roleAssignmentService.revokeRole(
                command,
                getClientIp(httpRequest),
                httpRequest.getHeader("User-Agent"));
        return ResponseEntity.noContent().build();
    }

    /**
     * Gets all available roles.
     * GET /api/v1/admin/users/roles
     * Implements Story 6: Role Assignment
     */
    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = roleAssignmentService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
