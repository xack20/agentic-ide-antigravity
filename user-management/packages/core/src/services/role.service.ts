import { inject, injectable } from 'tsyringe';
import {
    IRoleRepository,
    Role,
    CreateRoleDto,
    NotFoundError,
    ValidationError,
} from '@user-management/shared';

/**
 * Role service for role management
 */
@injectable()
export class RoleService {
    constructor(
        @inject('IRoleRepository') private readonly roleRepository: IRoleRepository
    ) { }

    /**
     * Get all roles
     */
    async getAllRoles(): Promise<Role[]> {
        return this.roleRepository.findAll();
    }

    /**
     * Get role by ID
     */
    async findById(id: string): Promise<Role | null> {
        return this.roleRepository.findById(id);
    }

    /**
     * Create a new role
     */
    async createRole(data: CreateRoleDto): Promise<Role> {
        const existing = await this.roleRepository.findByName(data.name);
        if (existing) {
            throw new ValidationError(`Role "${data.name}" already exists`);
        }

        return this.roleRepository.create({
            name: data.name,
            description: data.description,
            permissions: data.permissions || [],
        });
    }

    /**
     * Validate role IDs exist
     */
    async validateRoleIds(roleIds: string[]): Promise<boolean> {
        const roles = await this.roleRepository.findByIds(roleIds);
        if (roles.length !== roleIds.length) {
            const foundIds = new Set(roles.map(r => r.id));
            const invalidIds = roleIds.filter(id => !foundIds.has(id));
            throw new ValidationError(`Invalid role IDs: ${invalidIds.join(', ')}`);
        }
        return true;
    }

    /**
     * Seed default roles
     */
    async seedDefaultRoles(): Promise<void> {
        const defaultRoles = [
            { name: 'admin', description: 'Administrator with full access', permissions: ['*'] },
            { name: 'user', description: 'Regular user', permissions: ['read:own', 'write:own'] },
            { name: 'moderator', description: 'Moderator with limited admin access', permissions: ['read:all', 'moderate'] },
        ];

        for (const role of defaultRoles) {
            const existing = await this.roleRepository.findByName(role.name);
            if (!existing) {
                await this.roleRepository.create(role);
            }
        }
    }
}
