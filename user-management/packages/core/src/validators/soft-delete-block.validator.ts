import { inject, injectable } from 'tsyringe';
import {
    IValidator,
    IUserRepository,
    ValidationResult,
    validResult,
    invalidResult,
} from '@user-management/shared';

/**
 * Input for soft delete validation
 */
export interface SoftDeleteCheckInput {
    email: string;
    phoneNumber?: string;
}

/**
 * Validates that no soft-deleted account exists with the given email or phone
 */
@injectable()
export class SoftDeleteBlockValidator implements IValidator<SoftDeleteCheckInput> {
    constructor(
        @inject('IUserRepository') private readonly userRepository: IUserRepository
    ) { }

    async validate(data: SoftDeleteCheckInput): Promise<ValidationResult> {
        const normalizedEmail = data.email.toLowerCase().trim();

        // Check for deleted account with same email
        const deletedEmailExists = await this.userRepository.existsDeletedByEmail(normalizedEmail);
        if (deletedEmailExists) {
            return invalidResult(
                'email',
                'An account with this email was previously deleted. Please contact an admin to restore it.',
                'DELETED_EMAIL_EXISTS'
            );
        }

        // Check for deleted account with same phone (if provided)
        if (data.phoneNumber) {
            const deletedPhoneExists = await this.userRepository.existsDeletedByPhone(data.phoneNumber);
            if (deletedPhoneExists) {
                return invalidResult(
                    'phoneNumber',
                    'An account with this phone number was previously deleted. Please contact an admin to restore it.',
                    'DELETED_PHONE_EXISTS'
                );
            }
        }

        return validResult();
    }
}
