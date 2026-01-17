/**
 * Common weak passwords to block
 */
const WEAK_PASSWORDS = new Set([
    'password123',
    'password1234',
    'qwerty12345',
    '123456789a',
    'letmein123',
    'welcome123',
    'admin12345',
    'iloveyou123',
    'sunshine123',
    'princess123',
    'football123',
    'monkey12345',
    'shadow12345',
    'master12345',
    'dragon12345',
    'michael123',
    'jennifer123',
    'trustno123',
    'hunter12345',
    'password!23',
    'password@123',
    'abcd1234567',
    'qwertyuiop1',
    '1234567890!',
]);

/**
 * Validate password against policy rules
 * @returns Array of error messages, empty if valid
 */
export function validatePassword(
    password: string,
    email?: string,
    phoneNumber?: string
): string[] {
    const errors: string[] = [];

    // Min length: 10
    if (password.length < 10) {
        errors.push('Password must be at least 10 characters');
    }

    // Must include uppercase
    if (!/[A-Z]/.test(password)) {
        errors.push('Password must contain at least one uppercase letter');
    }

    // Must include lowercase
    if (!/[a-z]/.test(password)) {
        errors.push('Password must contain at least one lowercase letter');
    }

    // Must include number
    if (!/[0-9]/.test(password)) {
        errors.push('Password must contain at least one number');
    }

    // Must include special character
    if (!/[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(password)) {
        errors.push('Password must contain at least one special character');
    }

    // Must not contain email prefix
    if (email) {
        const emailPrefix = email.split('@')[0].toLowerCase();
        if (emailPrefix.length >= 3 && password.toLowerCase().includes(emailPrefix)) {
            errors.push('Password must not contain your email username');
        }
    }

    // Must not contain last 6 digits of phone
    if (phoneNumber) {
        const digitsOnly = phoneNumber.replace(/\D/g, '');
        if (digitsOnly.length >= 6) {
            const last6 = digitsOnly.slice(-6);
            if (password.includes(last6)) {
                errors.push('Password must not contain digits from your phone number');
            }
        }
    }

    // Must not be in weak password list
    if (WEAK_PASSWORDS.has(password.toLowerCase())) {
        errors.push('Password is too common, please choose a stronger password');
    }

    return errors;
}

/**
 * Validate Bangladesh phone number format (E.164)
 * Valid format: +8801XXXXXXXXX (14 characters total)
 */
export function validateBangladeshPhone(phone: string): boolean {
    // E.164 format for Bangladesh: +880 followed by operator code and number
    // Operators: 13, 14, 15, 16, 17, 18, 19
    const bdPhoneRegex = /^\+880(1[3-9])\d{8}$/;
    return bdPhoneRegex.test(phone);
}

/**
 * Validate name (first name or last name)
 * 2-50 chars, only letters, spaces, hyphen, apostrophe
 */
export function validateName(name: string): boolean {
    const nameRegex = /^[a-zA-Z\s\-']{2,50}$/;
    return nameRegex.test(name);
}

/**
 * Calculate age from date of birth
 */
export function calculateAge(dateOfBirth: Date): number {
    const today = new Date();
    let age = today.getFullYear() - dateOfBirth.getFullYear();
    const monthDiff = today.getMonth() - dateOfBirth.getMonth();

    if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < dateOfBirth.getDate())) {
        age--;
    }

    return age;
}

/**
 * Normalize email (lowercase, trim)
 */
export function normalizeEmail(email: string): string {
    return email.toLowerCase().trim();
}
