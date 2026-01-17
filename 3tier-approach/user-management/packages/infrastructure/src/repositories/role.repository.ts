import { injectable } from 'tsyringe';
import { IRoleRepository, Role } from '@user-management/shared';
import { RoleModel, RoleDocument } from '../entities/role.entity';

/**
 * MongoDB implementation of IRoleRepository
 */
@injectable()
export class RoleRepository implements IRoleRepository {
    private toEntity(doc: RoleDocument): Role {
        return {
            id: doc._id.toString(),
            name: doc.name,
            description: doc.description,
            permissions: doc.permissions,
            createdAt: doc.createdAt,
            updatedAt: doc.updatedAt,
        };
    }

    async findById(id: string): Promise<Role | null> {
        const doc = await RoleModel.findById(id);
        return doc ? this.toEntity(doc) : null;
    }

    async findAll(): Promise<Role[]> {
        const docs = await RoleModel.find();
        return docs.map((doc) => this.toEntity(doc));
    }

    async findOne(filter: Partial<Role>): Promise<Role | null> {
        const doc = await RoleModel.findOne(filter);
        return doc ? this.toEntity(doc) : null;
    }

    async findByName(name: string): Promise<Role | null> {
        const doc = await RoleModel.findOne({ name });
        return doc ? this.toEntity(doc) : null;
    }

    async findByIds(ids: string[]): Promise<Role[]> {
        const docs = await RoleModel.find({ _id: { $in: ids } });
        return docs.map((doc) => this.toEntity(doc));
    }

    async create(data: Omit<Role, 'id' | 'createdAt' | 'updatedAt'>): Promise<Role> {
        const doc = await RoleModel.create(data);
        return this.toEntity(doc);
    }

    async update(id: string, data: Partial<Role>): Promise<Role | null> {
        const doc = await RoleModel.findByIdAndUpdate(id, data, { new: true });
        return doc ? this.toEntity(doc) : null;
    }

    async delete(id: string): Promise<boolean> {
        const result = await RoleModel.findByIdAndDelete(id);
        return result !== null;
    }
}
