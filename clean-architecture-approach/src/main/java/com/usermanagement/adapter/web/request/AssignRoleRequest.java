package com.usermanagement.adapter.web.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for assigning a role.
 */
public record AssignRoleRequest(
        @NotBlank(message = "Role ID is required") String roleId) {
}
