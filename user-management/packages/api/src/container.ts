import 'reflect-metadata';
import { container } from 'tsyringe';
import { IUserRepository, IUserService, IValidator } from '@user-management/shared';
import { UserRepository } from '@user-management/infrastructure';
import {
    UserService,
    EmailUniquenessValidator,
    PhoneUniquenessValidator,
    SoftDeleteBlockValidator,
    SoftDeleteCheckInput
} from '@user-management/core';

/**
 * Configure dependency injection container
 */
export function configureContainer(): void {
    // Register repository
    container.register<IUserRepository>('IUserRepository', {
        useClass: UserRepository,
    });

    // Register validators
    container.register<IValidator<string>>('EmailUniquenessValidator', {
        useClass: EmailUniquenessValidator,
    });

    container.register<IValidator<string | undefined>>('PhoneUniquenessValidator', {
        useClass: PhoneUniquenessValidator,
    });

    container.register<IValidator<SoftDeleteCheckInput>>('SoftDeleteBlockValidator', {
        useClass: SoftDeleteBlockValidator,
    });

    // Register service
    container.register<IUserService>('IUserService', {
        useClass: UserService,
    });
}
