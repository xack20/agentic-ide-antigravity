import { z } from 'zod';
import {
    validatePassword,
    validateBangladeshPhone,
    validateName,
    calculateAge
} from './validation.utils';

/**
 * Zod schema for user registration request
 */
export const registerUserSchema = z.object({
    email: z
        .string()
        .email('Invalid email format')
        .min(1, 'Email is required')
        .transform((val) => val.toLowerCase().trim()),

    password: z
        .string()
        .min(10, 'Password must be at least 10 characters'),

    firstName: z
        .string()
        .min(2, 'First name must be at least 2 characters')
        .max(50, 'First name must be at most 50 characters')
        .refine(validateName, {
            message: 'First name can only contain letters, spaces, hyphens, and apostrophes',
        }),

    lastName: z
        .string()
        .min(2, 'Last name must be at least 2 characters')
        .max(50, 'Last name must be at most 50 characters')
        .refine(validateName, {
            message: 'Last name can only contain letters, spaces, hyphens, and apostrophes',
        }),

    displayName: z
        .string()
        .max(100, 'Display name must be at most 100 characters')
        .optional(),

    phoneNumber: z
        .string()
        .refine((val) => !val || validateBangladeshPhone(val), {
            message: 'Phone number must be in Bangladesh E.164 format (+8801XXXXXXXXX)',
        })
        .optional(),

    dateOfBirth: z
        .string()
        .optional()
        .refine(
            (val) => {
                if (!val) return true;
                const date = new Date(val);
                return !isNaN(date.getTime());
            },
            { message: 'Invalid date format' }
        )
        .refine(
            (val) => {
                if (!val) return true;
                const date = new Date(val);
                return calculateAge(date) >= 13;
            },
            { message: 'You must be at least 13 years old to register' }
        ),
}).superRefine((data, ctx) => {
    const passwordErrors = validatePassword(data.password, data.email, data.phoneNumber);
    for (const error of passwordErrors) {
        ctx.addIssue({
            code: z.ZodIssueCode.custom,
            message: error,
            path: ['password'],
        });
    }
});

export type RegisterUserInput = z.infer<typeof registerUserSchema>;

/**
 * Zod schema for user update request
 */
export const updateUserSchema = z.object({
    firstName: z
        .string()
        .min(2, 'First name must be at least 2 characters')
        .max(50, 'First name must be at most 50 characters')
        .refine(validateName, {
            message: 'First name can only contain letters, spaces, hyphens, and apostrophes',
        })
        .optional(),

    lastName: z
        .string()
        .min(2, 'Last name must be at least 2 characters')
        .max(50, 'Last name must be at most 50 characters')
        .refine(validateName, {
            message: 'Last name can only contain letters, spaces, hyphens, and apostrophes',
        })
        .optional(),

    displayName: z
        .string()
        .max(100, 'Display name must be at most 100 characters')
        .optional()
        .nullable(),

    phoneNumber: z
        .string()
        .refine((val) => !val || validateBangladeshPhone(val), {
            message: 'Phone number must be in Bangladesh E.164 format (+8801XXXXXXXXX)',
        })
        .optional()
        .nullable(),

    dateOfBirth: z
        .string()
        .optional()
        .nullable()
        .refine(
            (val) => {
                if (!val) return true;
                const date = new Date(val);
                return !isNaN(date.getTime());
            },
            { message: 'Invalid date format' }
        )
        .refine(
            (val) => {
                if (!val) return true;
                const date = new Date(val);
                return calculateAge(date) >= 13;
            },
            { message: 'You must be at least 13 years old' }
        ),

    isActive: z.boolean().optional(),
});

export type UpdateUserInput = z.infer<typeof updateUserSchema>;

/**
 * Zod schema for pagination query parameters
 */
export const paginationSchema = z.object({
    page: z.coerce.number().int().min(1).default(1),
    limit: z.coerce.number().int().min(1).max(100).default(10),
    sortBy: z.string().default('createdAt'),
    sortOrder: z.enum(['asc', 'desc']).default('desc'),
});

/**
 * Zod schema for get all users query
 */
export const getAllUsersSchema = paginationSchema.extend({
    includeDeleted: z.coerce.boolean().default(false),
});

export type GetAllUsersQuery = z.infer<typeof getAllUsersSchema>;

/**
 * Zod schema for user search query
 */
export const searchUsersSchema = paginationSchema.extend({
    q: z.string().optional(),
    email: z.string().optional(),
    phone: z.string().optional(),
    isActive: z.coerce.boolean().optional(),
});

export type SearchUsersQuery = z.infer<typeof searchUsersSchema>;

/**
 * Zod schema for change password request
 */
export const changePasswordSchema = z.object({
    currentPassword: z.string().min(1, 'Current password is required'),
    newPassword: z.string().min(10, 'Password must be at least 10 characters'),
    confirmPassword: z.string().min(1, 'Confirm password is required'),
}).refine((data) => data.newPassword === data.confirmPassword, {
    message: 'Passwords do not match',
    path: ['confirmPassword'],
}).refine((data) => data.newPassword !== data.currentPassword, {
    message: 'New password must be different from current password',
    path: ['newPassword'],
});

export type ChangePasswordInput = z.infer<typeof changePasswordSchema>;
