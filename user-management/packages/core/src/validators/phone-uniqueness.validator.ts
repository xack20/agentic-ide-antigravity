import { inject, injectable } from 'tsyringe';
import {
    IValidator,
    IUserRepository,
    ValidationResult,
    validResult,
    invalidResult,
} from '@user-management/shared';

/**
 * Validates that a phone number is not already registered
 */
@injectable()
export class PhoneUniquenessValidator implements IValidator<string | undefined> {
    constructor(
        @inject('IUserRepository') private readonly userRepository: IUserRepository
    ) { }

    async validate(phoneNumber: string | undefined): Promise<ValidationResult> {
        // Skip validation if phone not provided
        if (!phoneNumber) {
            return validResult();
        }

        const exists = await this.userRepository.existsByPhone(phoneNumber);

        if (exists) {
            return invalidResult('phoneNumber', 'Phone number already registered', 'PHONE_TAKEN');
        }

        return validResult();
    }
}
