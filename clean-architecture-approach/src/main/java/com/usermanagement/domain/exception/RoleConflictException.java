package com.usermanagement.domain.exception;

public class RoleConflictException extends DomainException {
    public RoleConflictException(String role1, String role2) {
        super("ROLE_CONFLICT",
                String.format("Role '%s' cannot be combined with role '%s'", role1, role2));
    }
}
