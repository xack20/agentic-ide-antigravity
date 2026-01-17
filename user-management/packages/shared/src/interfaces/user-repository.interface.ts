import { IRepository } from './repository.interface';
import { User } from '../types/user.types';
import { IPaginatedResult, IPaginationOptions } from './pagination.interface';
import { IUserSearchCriteria } from './search.interface';

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
    softDelete(id: string): Promise<boolean>;
    restore(id: string): Promise<User | null>;
    updatePassword(id: string, passwordHash: string): Promise<boolean>;
    findAllPaginated(filter: Partial<User>, options: IPaginationOptions): Promise<IPaginatedResult<User>>;
    search(criteria: IUserSearchCriteria, options: IPaginationOptions): Promise<IPaginatedResult<User>>;
}
