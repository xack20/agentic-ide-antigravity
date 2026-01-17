import { IRepository } from './repository.interface';
import { User } from '../types/user.types';

/**
 * User-specific repository interface extending generic repository
 */
export interface IUserRepository extends IRepository<User> {
    findByEmail(email: string): Promise<User | null>;
    findByPhone(phoneNumber: string): Promise<User | null>;
    existsByEmail(email: string, excludeDeleted?: boolean): Promise<boolean>;
    existsByPhone(phoneNumber: string, excludeDeleted?: boolean): Promise<boolean>;
    existsDeletedByEmail(email: string): Promise<boolean>;
    existsDeletedByPhone(phoneNumber: string): Promise<boolean>;
}
