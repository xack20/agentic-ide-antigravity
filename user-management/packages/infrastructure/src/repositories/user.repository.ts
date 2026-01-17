import { injectable } from 'tsyringe';
import { IUserRepository, User } from '@user-management/shared';
import { UserModel, UserDocument } from '../entities/user.entity';

/**
 * MongoDB implementation of IUserRepository
 */
@injectable()
export class UserRepository implements IUserRepository {
    /**
     * Convert Mongoose document to User entity
     */
    private toEntity(doc: UserDocument): User {
        return {
            id: doc._id.toString(),
            email: doc.email,
            passwordHash: doc.passwordHash,
            firstName: doc.firstName,
            lastName: doc.lastName,
            displayName: doc.displayName,
            phoneNumber: doc.phoneNumber,
            dateOfBirth: doc.dateOfBirth,
            isActive: doc.isActive,
            isDeleted: doc.isDeleted,
            createdAt: doc.createdAt,
            updatedAt: doc.updatedAt,
        };
    }

    async findById(id: string): Promise<User | null> {
        const doc = await UserModel.findById(id);
        return doc ? this.toEntity(doc) : null;
    }

    async findAll(): Promise<User[]> {
        const docs = await UserModel.find({ isDeleted: false });
        return docs.map((doc) => this.toEntity(doc));
    }

    async findOne(filter: Partial<User>): Promise<User | null> {
        const doc = await UserModel.findOne(filter);
        return doc ? this.toEntity(doc) : null;
    }

    async findByEmail(email: string): Promise<User | null> {
        const doc = await UserModel.findOne({
            email: email.toLowerCase(),
            isDeleted: false
        });
        return doc ? this.toEntity(doc) : null;
    }

    async findByPhone(phoneNumber: string): Promise<User | null> {
        const doc = await UserModel.findOne({
            phoneNumber,
            isDeleted: false
        });
        return doc ? this.toEntity(doc) : null;
    }

    async existsByEmail(email: string, excludeDeleted = true): Promise<boolean> {
        const query: any = { email: email.toLowerCase() };
        if (excludeDeleted) {
            query.isDeleted = false;
        }
        const count = await UserModel.countDocuments(query);
        return count > 0;
    }

    async existsByPhone(phoneNumber: string, excludeDeleted = true): Promise<boolean> {
        const query: any = { phoneNumber };
        if (excludeDeleted) {
            query.isDeleted = false;
        }
        const count = await UserModel.countDocuments(query);
        return count > 0;
    }

    async existsDeletedByEmail(email: string): Promise<boolean> {
        const count = await UserModel.countDocuments({
            email: email.toLowerCase(),
            isDeleted: true
        });
        return count > 0;
    }

    async existsDeletedByPhone(phoneNumber: string): Promise<boolean> {
        const count = await UserModel.countDocuments({
            phoneNumber,
            isDeleted: true
        });
        return count > 0;
    }

    async create(data: Omit<User, 'id' | 'createdAt' | 'updatedAt'>): Promise<User> {
        const doc = await UserModel.create(data);
        return this.toEntity(doc);
    }

    async update(id: string, data: Partial<User>): Promise<User | null> {
        const doc = await UserModel.findByIdAndUpdate(id, data, { new: true });
        return doc ? this.toEntity(doc) : null;
    }

    async delete(id: string): Promise<boolean> {
        // Soft delete
        const result = await UserModel.findByIdAndUpdate(id, { isDeleted: true });
        return result !== null;
    }
}
