/**
 * Base entity with common fields
 */
export interface BaseEntity {
    id: string;
    createdAt: Date;
    updatedAt: Date;
}

/**
 * User entity
 */
export interface User extends BaseEntity {
    email: string;
    passwordHash: string;
    firstName: string;
    lastName: string;
    displayName?: string;
    phoneNumber?: string;
    dateOfBirth?: Date;
    isActive: boolean;
    isDeleted: boolean;
}

/**
 * DTO for creating a new user
 */
export interface CreateUserDto {
    email: string;
    password: string;
    firstName: string;
    lastName: string;
    displayName?: string;
    phoneNumber?: string;
    dateOfBirth?: string; // ISO date string from API
}

/**
 * DTO for user response (excludes sensitive data)
 */
export interface UserResponseDto {
    id: string;
    email: string;
    firstName: string;
    lastName: string;
    displayName?: string;
    phoneNumber?: string;
    dateOfBirth?: Date;
    isActive: boolean;
    createdAt: Date;
}
