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
    // Cross-field password validation
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
 * All fields are optional, but if provided must meet same validation rules
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
