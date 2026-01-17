package com.usermanagement.domain.entity;

import com.usermanagement.domain.enums.Permission;
import com.usermanagement.domain.enums.RoleName;

import java.time.Instant;
import java.util.*;

/**
 * Role entity representing a set of permissions.
 */
public class Role {
    private final String id;
    private final RoleName name;
    private final String displayName;
    private final String description;
    private final int privilegeLevel;
    private final Set<Permission> permissions;
    private final RolePrerequisites prerequisites;
    private final Set<RoleName> excludesWith;
    private final boolean requiresDualApproval;
    private final Instant createdAt;
    private final Instant updatedAt;

    private Role(String id, RoleName name, String displayName, String description,
            int privilegeLevel, Set<Permission> permissions,
            RolePrerequisites prerequisites, Set<RoleName> excludesWith,
            boolean requiresDualApproval, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.privilegeLevel = privilegeLevel;
        this.permissions = permissions;
        this.prerequisites = prerequisites;
        this.excludesWith = excludesWith;
        this.requiresDualApproval = requiresDualApproval;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Role create(String id, RoleName name, String description,
            Set<Permission> permissions) {
        return new Role(
                id, name, name.getDisplayName(), description,
                name.getPrivilegeLevel(), permissions,
                RolePrerequisites.none(), Collections.emptySet(),
                false, Instant.now(), Instant.now());
    }

    public static Role reconstitute(String id, RoleName name, String displayName,
            String description, int privilegeLevel,
            Set<Permission> permissions,
            RolePrerequisites prerequisites,
            Set<RoleName> excludesWith,
            boolean requiresDualApproval,
            Instant createdAt, Instant updatedAt) {
        return new Role(id, name, displayName, description, privilegeLevel,
                permissions, prerequisites, excludesWith, requiresDualApproval,
                createdAt, updatedAt);
    }

    /**
     * Checks if this role has a specific permission.
     */
    public boolean hasPermission(Permission permission) {
        return permissions.contains(permission);
    }

    /**
     * Checks if this role can assign the target role.
     */
    public boolean canAssign(Role target) {
        return this.privilegeLevel > target.privilegeLevel;
    }

    /**
     * Checks if this role conflicts with another role.
     */
    public boolean conflictsWith(Role other) {
        return excludesWith.contains(other.name) || other.excludesWith.contains(this.name);
    }

    /**
     * Checks if a user meets prerequisites for this role.
     */
    public boolean userMeetsPrerequisites(User user) {
        return prerequisites.isSatisfiedBy(user);
    }

    public String getId() {
        return id;
    }

    public RoleName getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public int getPrivilegeLevel() {
        return privilegeLevel;
    }

    public Set<Permission> getPermissions() {
        return Collections.unmodifiableSet(permissions);
    }

    public RolePrerequisites getPrerequisites() {
        return prerequisites;
    }

    public Set<RoleName> getExcludesWith() {
        return Collections.unmodifiableSet(excludesWith);
    }

    public boolean requiresDualApproval() {
        return requiresDualApproval;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Role role = (Role) o;
        return Objects.equals(id, role.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
