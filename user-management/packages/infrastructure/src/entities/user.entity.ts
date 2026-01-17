import mongoose, { Schema, Document } from 'mongoose';
import { User } from '@user-management/shared';

/**
 * Mongoose document interface for User
 */
export interface UserDocument extends Omit<User, 'id'>, Document { }

/**
 * User Mongoose schema
 */
const userSchema = new Schema<UserDocument>(
    {
        email: {
            type: String,
            required: true,
            unique: true,
            lowercase: true,
            trim: true,
        },
        passwordHash: {
            type: String,
            required: true,
        },
        firstName: {
            type: String,
            required: true,
            trim: true,
        },
        lastName: {
            type: String,
            required: true,
            trim: true,
        },
        displayName: {
            type: String,
            trim: true,
        },
        phoneNumber: {
            type: String,
            sparse: true, // Allow null/undefined while maintaining uniqueness
            trim: true,
        },
        dateOfBirth: {
            type: Date,
        },
        isActive: {
            type: Boolean,
            default: true,
        },
        isDeleted: {
            type: Boolean,
            default: false,
        },
        deletedAt: {
            type: Date,
        },
        roles: [{
            type: Schema.Types.ObjectId,
            ref: 'Role',
        }],
    },
    {
        timestamps: true,
    }
);

// Compound unique index for phone number (only for non-null values)
userSchema.index({ phoneNumber: 1 }, { unique: true, sparse: true });

/**
 * User Mongoose model
 */
export const UserModel = mongoose.model<UserDocument>('User', userSchema);
