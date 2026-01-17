import { inject, injectable } from 'tsyringe';
import {
    IValidator,
    IUserRepository,
    ValidationResult,
    validResult,
    invalidResult,
} from '@user-management/shared';

/**
 * Validates that an email is not already registered
 */
@injectable()
export class EmailUniquenessValidator implements IValidator<string> {
    constructor(
        @inject('IUserRepository') private readonly userRepository: IUserRepository
    ) { }

    async validate(email: string): Promise<ValidationResult> {
        const normalizedEmail = email.toLowerCase().trim();
        const exists = await this.userRepository.existsByEmail(normalizedEmail);

        if (exists) {
            return invalidResult('email', 'Email already registered', 'EMAIL_TAKEN');
        }

        return validResult();
    }
}
