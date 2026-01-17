import 'reflect-metadata';
import { container } from 'tsyringe';
import { IUserRepository, IUserService, IValidator, IRoleRepository } from '@user-management/shared';
import { UserRepository, RoleRepository } from '@user-management/infrastructure';
import {
    UserService,
    RoleService,
    EmailUniquenessValidator,
    PhoneUniquenessValidator,
    SoftDeleteBlockValidator,
    SoftDeleteCheckInput
} from '@user-management/core';

/**
 * Configure dependency injection container
 */
export function configureContainer(): void {
    // Register repositories
    container.register<IUserRepository>('IUserRepository', {
        useClass: UserRepository,
    });

    container.register<IRoleRepository>('IRoleRepository', {
        useClass: RoleRepository,
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

    // Register services
    container.register<IUserService>('IUserService', {
        useClass: UserService,
    });

    container.register<RoleService>('RoleService', {
        useClass: RoleService,
    });
}
