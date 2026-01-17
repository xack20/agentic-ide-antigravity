/**
 * Pagination options for queries
 */
export interface IPaginationOptions {
    page: number;
    limit: number;
    sortBy?: string;
    sortOrder?: 'asc' | 'desc';
}

/**
 * Pagination metadata
 */
export interface IPaginationMeta {
    page: number;
    limit: number;
    totalItems: number;
    totalPages: number;
    hasNextPage: boolean;
    hasPrevPage: boolean;
}

/**
 * Generic paginated result
 */
export interface IPaginatedResult<T> {
    data: T[];
    pagination: IPaginationMeta;
}

/**
 * Default pagination values
 */
export const DEFAULT_PAGINATION: IPaginationOptions = {
    page: 1,
    limit: 10,
    sortBy: 'createdAt',
    sortOrder: 'desc',
};

/**
 * Maximum items per page
 */
export const MAX_PAGE_LIMIT = 100;
