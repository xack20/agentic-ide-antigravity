/**
 * Result of a validation operation
 */
export interface ValidationResult {
    isValid: boolean;
    errors: FieldValidationError[];
}

/**
 * Individual field validation error
 */
export interface FieldValidationError {
    field: string;
    message: string;
    code: string;
}

/**
 * Generic validator interface
 * @template T The type of data to validate
 */
export interface IValidator<T> {
    /**
     * Validate the given data
     * @param data The data to validate
     * @returns Validation result with errors if any
     */
    validate(data: T): Promise<ValidationResult>;
}

/**
 * Create a successful validation result
 */
export function validResult(): ValidationResult {
    return { isValid: true, errors: [] };
}

/**
 * Create a failed validation result
 */
export function invalidResult(
    field: string,
    message: string,
    code: string
): ValidationResult {
    return {
        isValid: false,
        errors: [{ field, message, code }],
    };
}
