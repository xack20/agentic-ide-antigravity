import { IRepository } from './repository.interface';
import { Role } from '../types/role.types';

/**
 * Role-specific repository interface
 */
export interface IRoleRepository extends IRepository<Role> {
    findByName(name: string): Promise<Role | null>;
    findByIds(ids: string[]): Promise<Role[]>;
}
