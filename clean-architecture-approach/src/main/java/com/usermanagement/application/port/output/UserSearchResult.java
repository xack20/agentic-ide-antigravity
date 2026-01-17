package com.usermanagement.application.port.output;

import com.usermanagement.domain.entity.User;

import java.util.List;

/**
 * Paginated result for user search.
 */
public class UserSearchResult {
    private final List<User> users;
    private final long totalCount;
    private final int page;
    private final int pageSize;
    private final int totalPages;

    public UserSearchResult(List<User> users, long totalCount, int page, int pageSize) {
        this.users = users;
        this.totalCount = totalCount;
        this.page = page;
        this.pageSize = pageSize;
        this.totalPages = (int) Math.ceil((double) totalCount / pageSize);
    }

    public List<User> getUsers() {
        return users;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public boolean hasNext() {
        return page < totalPages - 1;
    }

    public boolean hasPrevious() {
        return page > 0;
    }
}
