import { BaseEntity } from './user.types';

/**
 * Role entity
 */
export interface Role extends BaseEntity {
    name: string;
    description: string;
    permissions: string[];
}

/**
 * DTO for creating a new role
 */
export interface CreateRoleDto {
    name: string;
    description: string;
    permissions?: string[];
}

/**
 * DTO for assigning roles to a user
 */
export interface AssignRolesDto {
    roleIds: string[];
}
