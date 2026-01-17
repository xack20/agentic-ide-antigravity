import { injectable } from 'tsyringe';
import {
    IUserRepository,
    User,
    IPaginatedResult,
    IPaginationOptions,
    IUserSearchCriteria
} from '@user-management/shared';
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
            deletedAt: doc.deletedAt,
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
        // Soft delete with deletedAt timestamp
        const result = await UserModel.findByIdAndUpdate(id, {
            isDeleted: true,
            deletedAt: new Date()
        });
        return result !== null;
    }

    async softDelete(id: string): Promise<boolean> {
        const doc = await UserModel.findById(id);
        if (!doc || doc.isDeleted) {
            return false;
        }
        await UserModel.findByIdAndUpdate(id, {
            isDeleted: true,
            deletedAt: new Date()
        });
        return true;
    }

    async restore(id: string): Promise<User | null> {
        const doc = await UserModel.findById(id);
        if (!doc || !doc.isDeleted) {
            return null;
        }
        const updated = await UserModel.findByIdAndUpdate(
            id,
            { isDeleted: false, deletedAt: null },
            { new: true }
        );
        return updated ? this.toEntity(updated) : null;
    }

    async updatePassword(id: string, passwordHash: string): Promise<boolean> {
        const result = await UserModel.findByIdAndUpdate(id, { passwordHash });
        return result !== null;
    }

    async findAllPaginated(
        filter: Partial<User>,
        options: IPaginationOptions
    ): Promise<IPaginatedResult<User>> {
        const { page, limit, sortBy = 'createdAt', sortOrder = 'desc' } = options;
        const skip = (page - 1) * limit;

        const sortDirection = sortOrder === 'asc' ? 1 : -1;

        const [docs, totalItems] = await Promise.all([
            UserModel.find(filter)
                .sort({ [sortBy]: sortDirection })
                .skip(skip)
                .limit(limit),
            UserModel.countDocuments(filter),
        ]);

        const totalPages = Math.ceil(totalItems / limit);

        return {
            data: docs.map((doc) => this.toEntity(doc)),
            pagination: {
                page,
                limit,
                totalItems,
                totalPages,
                hasNextPage: page < totalPages,
                hasPrevPage: page > 1,
            },
        };
    }

    async search(
        criteria: IUserSearchCriteria,
        options: IPaginationOptions
    ): Promise<IPaginatedResult<User>> {
        const filter: any = { isDeleted: false };

        // General search term (name or email)
        if (criteria.q) {
            const searchRegex = new RegExp(criteria.q, 'i');
            filter.$or = [
                { firstName: searchRegex },
                { lastName: searchRegex },
                { email: searchRegex },
                { displayName: searchRegex },
            ];
        }

        // Partial email match
        if (criteria.email) {
            filter.email = new RegExp(criteria.email, 'i');
        }

        // Partial phone match
        if (criteria.phone) {
            filter.phoneNumber = new RegExp(criteria.phone, 'i');
        }

        // Filter by active status
        if (criteria.isActive !== undefined) {
            filter.isActive = criteria.isActive;
        }

        return this.findAllPaginated(filter, options);
    }
}
