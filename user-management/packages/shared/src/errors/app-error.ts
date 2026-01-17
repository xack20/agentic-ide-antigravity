/**
 * Base application error
 */
export class AppError extends Error {
    constructor(
        public readonly message: string,
        public readonly statusCode: number = 500,
        public readonly code: string = 'INTERNAL_ERROR'
    ) {
        super(message);
        this.name = this.constructor.name;
        if (Error.captureStackTrace) {
            Error.captureStackTrace(this, this.constructor);
        }
    }
}

/**
 * Entity not found error
 */
export class NotFoundError extends AppError {
    constructor(entity: string, id: string) {
        super(`${entity} with id '${id}' not found`, 404, 'NOT_FOUND');
    }
}

/**
 * Validation error
 */
export class ValidationError extends AppError {
    constructor(message: string) {
        super(message, 400, 'VALIDATION_ERROR');
    }
}

/**
 * Conflict error (e.g., duplicate email)
 */
export class ConflictError extends AppError {
    constructor(message: string) {
        super(message, 409, 'CONFLICT');
    }
}
