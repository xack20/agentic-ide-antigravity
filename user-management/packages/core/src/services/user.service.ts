import { inject, injectable } from 'tsyringe';
import bcrypt from 'bcrypt';
import {
    IUserService,
    IUserRepository,
    IValidator,
    CreateUserDto,
    UpdateUserDto,
    UserResponseDto,
    ChangePasswordDto,
    ConflictError,
    NotFoundError,
    ValidationError,
    IPaginatedResult,
    IPaginationOptions,
    IUserSearchCriteria,
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

        await this.runRegistrationValidators(normalizedEmail, data.phoneNumber);

        const passwordHash = await bcrypt.hash(data.password, this.SALT_ROUNDS);

        let dateOfBirth: Date | undefined;
        if (data.dateOfBirth) {
            dateOfBirth = new Date(data.dateOfBirth);
        }

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
    private async runRegistrationValidators(email: string, phoneNumber?: string): Promise<void> {
        const emailResult = await this.emailValidator.validate(email);
        if (!emailResult.isValid) {
            throw new ConflictError(emailResult.errors[0].message);
        }

        const phoneResult = await this.phoneValidator.validate(phoneNumber);
        if (!phoneResult.isValid) {
            throw new ConflictError(phoneResult.errors[0].message);
        }

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
     * Update user profile
     */
    async update(id: string, data: UpdateUserDto): Promise<UserResponseDto> {
        const existingUser = await this.userRepository.findById(id);
        if (!existingUser || existingUser.isDeleted) {
            throw new NotFoundError('User', id);
        }

        if (data.phoneNumber !== undefined && data.phoneNumber !== existingUser.phoneNumber) {
            const phoneResult = await this.phoneValidator.validate(data.phoneNumber);
            if (!phoneResult.isValid) {
                throw new ConflictError(phoneResult.errors[0].message);
            }
        }

        const updateData: Partial<{
            firstName: string;
            lastName: string;
            displayName?: string;
            phoneNumber?: string;
            dateOfBirth?: Date;
            isActive: boolean;
        }> = {};

        if (data.firstName !== undefined) updateData.firstName = data.firstName.trim();
        if (data.lastName !== undefined) updateData.lastName = data.lastName.trim();
        if (data.displayName !== undefined) updateData.displayName = data.displayName?.trim() || undefined;
        if (data.phoneNumber !== undefined) updateData.phoneNumber = data.phoneNumber || undefined;
        if (data.dateOfBirth !== undefined) updateData.dateOfBirth = data.dateOfBirth ? new Date(data.dateOfBirth) : undefined;
        if (data.isActive !== undefined) updateData.isActive = data.isActive;

        const updatedUser = await this.userRepository.update(id, updateData);
        if (!updatedUser) {
            throw new NotFoundError('User', id);
        }

        return this.toResponseDto(updatedUser);
    }

    /**
     * Soft delete a user
     */
    async softDelete(id: string): Promise<boolean> {
        const user = await this.userRepository.findById(id);
        if (!user || user.isDeleted) {
            return false;
        }
        return this.userRepository.softDelete(id);
    }

    /**
     * Restore a soft-deleted user
     */
    async restoreUser(id: string): Promise<UserResponseDto> {
        const user = await this.userRepository.findById(id);
        if (!user) {
            throw new NotFoundError('User', id);
        }
        if (!user.isDeleted) {
            throw new ValidationError('User is not deleted');
        }

        // Check email/phone uniqueness before restoring
        const emailResult = await this.emailValidator.validate(user.email);
        if (!emailResult.isValid) {
            throw new ConflictError('Cannot restore: ' + emailResult.errors[0].message);
        }

        if (user.phoneNumber) {
            const phoneResult = await this.phoneValidator.validate(user.phoneNumber);
            if (!phoneResult.isValid) {
                throw new ConflictError('Cannot restore: ' + phoneResult.errors[0].message);
            }
        }

        const restored = await this.userRepository.restore(id);
        if (!restored) {
            throw new NotFoundError('User', id);
        }

        return this.toResponseDto(restored);
    }

    /**
     * Change user password
     */
    async changePassword(id: string, data: ChangePasswordDto): Promise<void> {
        if (data.newPassword !== data.confirmPassword) {
            throw new ValidationError('New password and confirmation do not match');
        }

        if (data.newPassword === data.currentPassword) {
            throw new ValidationError('New password must be different from current password');
        }

        const user = await this.userRepository.findById(id);
        if (!user || user.isDeleted) {
            throw new NotFoundError('User', id);
        }

        const isCurrentValid = await bcrypt.compare(data.currentPassword, user.passwordHash);
        if (!isCurrentValid) {
            throw new ValidationError('Current password is incorrect');
        }

        const newHash = await bcrypt.hash(data.newPassword, this.SALT_ROUNDS);
        await this.userRepository.updatePassword(id, newHash);
    }

    /**
     * Get all users with pagination
     */
    async getAllUsers(
        options: IPaginationOptions,
        includeDeleted = false
    ): Promise<IPaginatedResult<UserResponseDto>> {
        const { page, limit, sortBy = 'createdAt', sortOrder = 'desc' } = options;

        const filter: any = {};
        if (!includeDeleted) {
            filter.isDeleted = false;
        }

        const result = await this.userRepository.findAllPaginated(filter, {
            page,
            limit,
            sortBy,
            sortOrder,
        });

        return {
            data: result.data.map((u) => this.toResponseDto(u)),
            pagination: result.pagination,
        };
    }

    /**
     * Search users
     */
    async searchUsers(
        criteria: IUserSearchCriteria,
        options: IPaginationOptions
    ): Promise<IPaginatedResult<UserResponseDto>> {
        const result = await this.userRepository.search(criteria, options);

        return {
            data: result.data.map((u) => this.toResponseDto(u)),
            pagination: result.pagination,
        };
    }

    /**
     * Convert User entity to response DTO
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
