package com.usermanagement.adapter.persistence.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Set;

/**
 * MongoDB document for Role entity.
 */
@Document(collection = "roles")
public class RoleDocument {
    @Id
    private String id;
    private String name;
    private String displayName;
    private String description;
    private int privilegeLevel;
    private Set<String> permissions;
    private PrerequisitesDocument prerequisites;
    private Set<String> excludesWith;
    private boolean requiresDualApproval;
    private boolean isActive;
    private boolean isSystemRole;
    private boolean isDefaultRole;
    private Instant createdAt;
    private Instant updatedAt;

    public record PrerequisitesDocument(
            String requiredStatus,
            boolean requiresEmailVerified,
            boolean requiresPhoneVerified) {
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrivilegeLevel() {
        return privilegeLevel;
    }

    public void setPrivilegeLevel(int privilegeLevel) {
        this.privilegeLevel = privilegeLevel;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }

    public PrerequisitesDocument getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(PrerequisitesDocument prerequisites) {
        this.prerequisites = prerequisites;
    }

    public Set<String> getExcludesWith() {
        return excludesWith;
    }

    public void setExcludesWith(Set<String> excludesWith) {
        this.excludesWith = excludesWith;
    }

    public boolean isRequiresDualApproval() {
        return requiresDualApproval;
    }

    public void setRequiresDualApproval(boolean requiresDualApproval) {
        this.requiresDualApproval = requiresDualApproval;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isSystemRole() {
        return isSystemRole;
    }

    public void setSystemRole(boolean systemRole) {
        isSystemRole = systemRole;
    }

    public boolean isDefaultRole() {
        return isDefaultRole;
    }

    public void setDefaultRole(boolean defaultRole) {
        isDefaultRole = defaultRole;
    }
}
