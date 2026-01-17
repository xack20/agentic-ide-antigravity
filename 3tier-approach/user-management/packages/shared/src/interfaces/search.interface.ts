/**
 * Search criteria for users
 */
export interface IUserSearchCriteria {
    q?: string;           // General search term (name, email)
    email?: string;       // Partial email match
    phone?: string;       // Partial phone match
    isActive?: boolean;   // Filter by active status
}
