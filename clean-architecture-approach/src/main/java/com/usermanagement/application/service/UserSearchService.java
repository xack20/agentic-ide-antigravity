package com.usermanagement.application.service;

import com.usermanagement.application.dto.response.UserProfileResponse;
import com.usermanagement.application.port.output.*;
import com.usermanagement.domain.entity.Role;
import com.usermanagement.domain.entity.User;
import com.usermanagement.domain.enums.Permission;
import com.usermanagement.domain.exception.RateLimitExceededException;
import com.usermanagement.domain.exception.UnauthorizedException;
import com.usermanagement.domain.exception.UserNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Use case service for searching and listing users.
 * Implements Story 7: Search / List Users (Basic Admin)
 */
@Service
public class UserSearchService {

    private static final int MAX_PAGE_SIZE = 50;
    private static final int MIN_QUERY_LENGTH = 3;
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "createdAt", "updatedAt", "email", "fullName", "status");

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RateLimiter rateLimiter;

    public UserSearchService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            RateLimiter rateLimiter) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.rateLimiter = rateLimiter;
    }

    /**
     * Searches users with filtering and pagination.
     * 
     * AC1: Only authorized admins can list/search users.
     * AC2: Requests must include paging; page size beyond max is rejected or
     * clamped.
     * AC3: Search enforces query constraints.
     * AC4: Results are masked unless admin has elevated permission.
     * AC5: Sorting/filtering validation prevents invalid or expensive queries.
     * AC6: Admin searches are rate-limited.
     *
     * @param criteria    Search criteria
     * @param requesterId The admin making the request
     */
    public SearchResult execute(UserSearchCriteria criteria, String requesterId) {
        // Get requester
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new UserNotFoundException(requesterId));

        // Check authorization (AC1)
        if (!hasPermission(requester, Permission.USER_READ)) {
            throw new UnauthorizedException("You do not have permission to search users");
        }

        // Rate limiting (AC6)
        if (!rateLimiter.tryConsume(requesterId, "admin-search")) {
            throw new RateLimitExceededException("admin search");
        }

        // Validate and sanitize criteria
        UserSearchCriteria sanitized = sanitizeCriteria(criteria);

        // Execute search
        UserSearchResult result = userRepository.search(sanitized);

        // Check if user has elevated permission for unmasked data
        boolean hasExportPermission = hasPermission(requester, Permission.USER_EXPORT);

        // Map results with appropriate masking (AC4)
        List<UserProfileResponse> mappedUsers = result.getUsers().stream()
                .map(user -> UserProfileResponse.from(user, !hasExportPermission))
                .toList();

        return new SearchResult(
                mappedUsers,
                result.getTotalCount(),
                result.getPage(),
                result.getPageSize(),
                result.getTotalPages());
    }

    private UserSearchCriteria sanitizeCriteria(UserSearchCriteria criteria) {
        UserSearchCriteria.Builder builder = UserSearchCriteria.builder();

        // Validate search query (AC3)
        if (criteria.getSearchQuery() != null) {
            String query = criteria.getSearchQuery().trim();

            // Prevent empty or too short queries
            if (query.length() > 0 && query.length() < MIN_QUERY_LENGTH) {
                throw new IllegalArgumentException(
                        "Search query must be at least " + MIN_QUERY_LENGTH + " characters");
            }

            // Prevent leading wildcards (expensive queries)
            if (query.startsWith("*") || query.startsWith("%")) {
                throw new IllegalArgumentException("Search query cannot start with wildcard");
            }

            builder.searchQuery(query);
        }

        // Copy over filter criteria
        if (criteria.getStatuses() != null && !criteria.getStatuses().isEmpty()) {
            builder.statuses(criteria.getStatuses());
        }
        if (criteria.getRoleIds() != null && !criteria.getRoleIds().isEmpty()) {
            builder.roleIds(criteria.getRoleIds());
        }
        if (criteria.getEmailVerified() != null) {
            builder.emailVerified(criteria.getEmailVerified());
        }

        // Validate date range
        if (criteria.getCreatedAfter() != null && criteria.getCreatedBefore() != null) {
            if (criteria.getCreatedAfter().isAfter(criteria.getCreatedBefore())) {
                throw new IllegalArgumentException("createdAfter must be before createdBefore");
            }
        }
        builder.createdAfter(criteria.getCreatedAfter());
        builder.createdBefore(criteria.getCreatedBefore());

        // Validate and set sort field (AC5)
        String sortField = criteria.getSortField();
        if (sortField != null && !ALLOWED_SORT_FIELDS.contains(sortField)) {
            throw new IllegalArgumentException("Invalid sort field: " + sortField +
                    ". Allowed: " + ALLOWED_SORT_FIELDS);
        }
        builder.sortField(sortField != null ? sortField : "createdAt");
        builder.sortDirection(criteria.getSortDirection());

        // Clamp page size (AC2)
        int pageSize = criteria.getPageSize();
        if (pageSize > MAX_PAGE_SIZE) {
            pageSize = MAX_PAGE_SIZE;
        }
        builder.page(criteria.getPage());
        builder.pageSize(pageSize);

        return builder.build();
    }

    private boolean hasPermission(User user, Permission permission) {
        List<Role> roles = roleRepository.findByIds(user.getRoleIds());
        return roles.stream().anyMatch(role -> role.hasPermission(permission));
    }

    /**
     * Result wrapper for user search.
     */
    public record SearchResult(
            List<UserProfileResponse> users,
            long totalCount,
            int page,
            int pageSize,
            int totalPages) {
    }
}
