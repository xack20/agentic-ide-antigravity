package com.usermanagement.application.port.output;

import com.usermanagement.domain.enums.UserStatus;

import java.time.Instant;
import java.util.List;
import java.util.Set;

/**
 * Criteria for searching users.
 */
public class UserSearchCriteria {
    private String searchQuery; // Search in name, email, phone
    private Set<UserStatus> statuses;
    private Set<String> roleIds;
    private Boolean emailVerified;
    private Instant createdAfter;
    private Instant createdBefore;
    private String sortField;
    private SortDirection sortDirection;
    private int page;
    private int pageSize;

    public enum SortDirection {
        ASC, DESC
    }

    // Private constructor - use builder
    private UserSearchCriteria() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public Set<UserStatus> getStatuses() {
        return statuses;
    }

    public Set<String> getRoleIds() {
        return roleIds;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public Instant getCreatedAfter() {
        return createdAfter;
    }

    public Instant getCreatedBefore() {
        return createdBefore;
    }

    public String getSortField() {
        return sortField;
    }

    public SortDirection getSortDirection() {
        return sortDirection;
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getOffset() {
        return page * pageSize;
    }

    public static class Builder {
        private final UserSearchCriteria criteria = new UserSearchCriteria();

        public Builder searchQuery(String query) {
            criteria.searchQuery = query;
            return this;
        }

        public Builder statuses(Set<UserStatus> statuses) {
            criteria.statuses = statuses;
            return this;
        }

        public Builder roleIds(Set<String> roleIds) {
            criteria.roleIds = roleIds;
            return this;
        }

        public Builder emailVerified(Boolean verified) {
            criteria.emailVerified = verified;
            return this;
        }

        public Builder createdAfter(Instant after) {
            criteria.createdAfter = after;
            return this;
        }

        public Builder createdBefore(Instant before) {
            criteria.createdBefore = before;
            return this;
        }

        public Builder sortField(String field) {
            criteria.sortField = field;
            return this;
        }

        public Builder sortDirection(SortDirection direction) {
            criteria.sortDirection = direction;
            return this;
        }

        public Builder page(int page) {
            criteria.page = Math.max(0, page);
            return this;
        }

        public Builder pageSize(int size) {
            criteria.pageSize = Math.min(Math.max(1, size), 50); // Max 50
            return this;
        }

        public UserSearchCriteria build() {
            if (criteria.pageSize == 0) {
                criteria.pageSize = 20; // Default
            }
            if (criteria.sortDirection == null) {
                criteria.sortDirection = SortDirection.DESC;
            }
            if (criteria.sortField == null) {
                criteria.sortField = "createdAt";
            }
            return criteria;
        }
    }
}
