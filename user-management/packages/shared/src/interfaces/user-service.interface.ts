import { CreateUserDto, UserResponseDto } from '../types/user.types';

/**
 * User service interface for business operations
 */
export interface IUserService {
    register(data: CreateUserDto): Promise<UserResponseDto>;
    findById(id: string): Promise<UserResponseDto | null>;
    findByEmail(email: string): Promise<UserResponseDto | null>;
}
