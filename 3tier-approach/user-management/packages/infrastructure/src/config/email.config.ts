import { IEmailConfig } from '@user-management/shared';

/**
 * Load email configuration from environment variables
 */
export function loadEmailConfig(): IEmailConfig {
    return {
        host: process.env.EMAIL_HOST || 'smtp.ethereal.email',
        port: parseInt(process.env.EMAIL_PORT || '587', 10),
        secure: process.env.EMAIL_SECURE === 'true',
        auth: {
            user: process.env.EMAIL_USER || '',
            pass: process.env.EMAIL_PASS || '',
        },
        from: process.env.EMAIL_FROM || 'User Management <noreply@example.com>',
    };
}

/**
 * Check if email is configured
 */
export function isEmailConfigured(): boolean {
    return !!(process.env.EMAIL_USER && process.env.EMAIL_PASS);
}
