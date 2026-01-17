import 'reflect-metadata';
import { container } from 'tsyringe';
import { IUserRepository, IUserService } from '@user-management/shared';
import { UserRepository } from '@user-management/infrastructure';
import { UserService } from '@user-management/core';

/**
 * Configure dependency injection container
 */
export function configureContainer(): void {
    // Register repository
    container.register<IUserRepository>('IUserRepository', {
        useClass: UserRepository,
    });

    // Register service
    container.register<IUserService>('IUserService', {
        useClass: UserService,
    });
}
