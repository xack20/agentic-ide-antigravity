import { CreateUserDto, UpdateUserDto, UserResponseDto } from '../types/user.types';
import { IPaginatedResult, IPaginationOptions } from './pagination.interface';
import { IUserSearchCriteria } from './search.interface';

/**
 * DTO for changing password
 */
export interface ChangePasswordDto {
    currentPassword: string;
    newPassword: string;
    confirmPassword: string;
}

/**
 * User service interface for business operations
 */
export interface IUserService {
    register(data: CreateUserDto): Promise<UserResponseDto>;
    update(id: string, data: UpdateUserDto): Promise<UserResponseDto>;
    findById(id: string): Promise<UserResponseDto | null>;
    findByEmail(email: string): Promise<UserResponseDto | null>;
    softDelete(id: string): Promise<boolean>;
    restoreUser(id: string): Promise<UserResponseDto>;
    changePassword(id: string, data: ChangePasswordDto): Promise<void>;
    getAllUsers(options: IPaginationOptions, includeDeleted?: boolean): Promise<IPaginatedResult<UserResponseDto>>;
    searchUsers(criteria: IUserSearchCriteria, options: IPaginationOptions): Promise<IPaginatedResult<UserResponseDto>>;
}
