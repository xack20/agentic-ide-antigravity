import { inject, injectable } from 'tsyringe';
import bcrypt from 'bcrypt';
import {
    IUserService,
    IUserRepository,
    CreateUserDto,
    UserResponseDto,
    ConflictError,
    ValidationError,
} from '@user-management/shared';

/**
 * User service implementing business logic
 */
@injectable()
export class UserService implements IUserService {
    private readonly SALT_ROUNDS = 10;

    constructor(
        @inject('IUserRepository') private readonly userRepository: IUserRepository
    ) { }

    /**
     * Register a new user
     */
    async register(data: CreateUserDto): Promise<UserResponseDto> {
        const normalizedEmail = data.email.toLowerCase().trim();

        // Check if email exists (active user)
        const emailExists = await this.userRepository.existsByEmail(normalizedEmail);
        if (emailExists) {
            throw new ConflictError('Email already registered');
        }

        // Check if soft-deleted account exists with this email
        const deletedEmailExists = await this.userRepository.existsDeletedByEmail(normalizedEmail);
        if (deletedEmailExists) {
            throw new ConflictError(
                'An account with this email was previously deleted. Please contact an admin to restore it.'
            );
        }

        // Phone number uniqueness checks (if provided)
        if (data.phoneNumber) {
            const phoneExists = await this.userRepository.existsByPhone(data.phoneNumber);
            if (phoneExists) {
                throw new ConflictError('Phone number already registered');
            }

            const deletedPhoneExists = await this.userRepository.existsDeletedByPhone(data.phoneNumber);
            if (deletedPhoneExists) {
                throw new ConflictError(
                    'An account with this phone number was previously deleted. Please contact an admin to restore it.'
                );
            }
        }

        // Hash password using bcrypt
        const passwordHash = await bcrypt.hash(data.password, this.SALT_ROUNDS);

        // Parse date of birth if provided
        let dateOfBirth: Date | undefined;
        if (data.dateOfBirth) {
            dateOfBirth = new Date(data.dateOfBirth);
        }

        // Create user
        const user = await this.userRepository.create({
            email: normalizedEmail,
            passwordHash,
            firstName: data.firstName.trim(),
            lastName: data.lastName.trim(),
            displayName: data.displayName?.trim(),
            phoneNumber: data.phoneNumber,
            dateOfBirth,
            isActive: true,
            isDeleted: false,
        });

        return this.toResponseDto(user);
    }

    /**
     * Find user by ID
     */
    async findById(id: string): Promise<UserResponseDto | null> {
        const user = await this.userRepository.findById(id);
        if (!user || user.isDeleted) {
            return null;
        }
        return this.toResponseDto(user);
    }

    /**
     * Find user by email
     */
    async findByEmail(email: string): Promise<UserResponseDto | null> {
        const user = await this.userRepository.findByEmail(email);
        if (!user || user.isDeleted) {
            return null;
        }
        return this.toResponseDto(user);
    }

    /**
     * Convert User entity to response DTO (exclude sensitive data)
     */
    private toResponseDto(user: {
        id: string;
        email: string;
        firstName: string;
        lastName: string;
        displayName?: string;
        phoneNumber?: string;
        dateOfBirth?: Date;
        isActive: boolean;
        createdAt: Date;
    }): UserResponseDto {
        return {
            id: user.id,
            email: user.email,
            firstName: user.firstName,
            lastName: user.lastName,
            displayName: user.displayName,
            phoneNumber: user.phoneNumber,
            dateOfBirth: user.dateOfBirth,
            isActive: user.isActive,
            createdAt: user.createdAt,
        };
    }
}
