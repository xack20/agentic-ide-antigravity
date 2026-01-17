package com.usermanagement.adapter.web.request;

import com.usermanagement.domain.enums.UserStatus;

import java.time.Instant;
import java.util.Set;

/**
 * Request DTO for user search.
 */
public record UserSearchRequest(
        String query,
        Set<UserStatus> statuses,
        Set<String> roleIds,
        Boolean emailVerified,
        Instant createdAfter,
        Instant createdBefore,
        String sortField,
        String sortDirection,
        Integer page,
        Integer pageSize) {
    public static UserSearchRequest defaults() {
        return new UserSearchRequest(null, null, null, null, null, null,
                "createdAt", "DESC", 0, 20);
    }
}
