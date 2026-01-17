import { inject, injectable } from 'tsyringe';
import bcrypt from 'bcrypt';
import {
    IUserService,
    IUserRepository,
    IValidator,
    CreateUserDto,
    UserResponseDto,
    ConflictError,
} from '@user-management/shared';
import { SoftDeleteCheckInput } from '../validators/soft-delete-block.validator';

/**
 * User service implementing business logic
 */
@injectable()
export class UserService implements IUserService {
    private readonly SALT_ROUNDS = 10;

    constructor(
        @inject('IUserRepository') private readonly userRepository: IUserRepository,
        @inject('EmailUniquenessValidator') private readonly emailValidator: IValidator<string>,
        @inject('PhoneUniquenessValidator') private readonly phoneValidator: IValidator<string | undefined>,
        @inject('SoftDeleteBlockValidator') private readonly softDeleteValidator: IValidator<SoftDeleteCheckInput>
    ) { }

    /**
     * Register a new user
     */
    async register(data: CreateUserDto): Promise<UserResponseDto> {
        const normalizedEmail = data.email.toLowerCase().trim();

        // Run validators
        await this.runValidators(normalizedEmail, data.phoneNumber);

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
     * Run all validators for registration
     */
    private async runValidators(email: string, phoneNumber?: string): Promise<void> {
        // Check email uniqueness
        const emailResult = await this.emailValidator.validate(email);
        if (!emailResult.isValid) {
            throw new ConflictError(emailResult.errors[0].message);
        }

        // Check phone uniqueness (if provided)
        const phoneResult = await this.phoneValidator.validate(phoneNumber);
        if (!phoneResult.isValid) {
            throw new ConflictError(phoneResult.errors[0].message);
        }

        // Check soft-deleted accounts
        const softDeleteResult = await this.softDeleteValidator.validate({ email, phoneNumber });
        if (!softDeleteResult.isValid) {
            throw new ConflictError(softDeleteResult.errors[0].message);
        }
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
