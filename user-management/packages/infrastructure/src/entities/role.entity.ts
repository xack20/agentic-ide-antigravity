import mongoose, { Schema, Document } from 'mongoose';
import { Role } from '@user-management/shared';

/**
 * Mongoose document interface for Role
 */
export interface RoleDocument extends Omit<Role, 'id'>, Document { }

/**
 * Role Mongoose schema
 */
const roleSchema = new Schema<RoleDocument>(
    {
        name: {
            type: String,
            required: true,
            unique: true,
            trim: true,
        },
        description: {
            type: String,
            required: true,
            trim: true,
        },
        permissions: {
            type: [String],
            default: [],
        },
    },
    {
        timestamps: true,
    }
);

/**
 * Role Mongoose model
 */
export const RoleModel = mongoose.model<RoleDocument>('Role', roleSchema);
