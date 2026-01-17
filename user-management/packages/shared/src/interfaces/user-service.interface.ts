import { CreateUserDto, UpdateUserDto, UserResponseDto } from '../types/user.types';

/**
 * User service interface for business operations
 */
export interface IUserService {
    register(data: CreateUserDto): Promise<UserResponseDto>;
    update(id: string, data: UpdateUserDto): Promise<UserResponseDto>;
    findById(id: string): Promise<UserResponseDto | null>;
    findByEmail(email: string): Promise<UserResponseDto | null>;
}
