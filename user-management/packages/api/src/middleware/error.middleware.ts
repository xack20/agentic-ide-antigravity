import { Request, Response, NextFunction } from 'express';
import { AppError } from '@user-management/shared';

/**
 * Global error handling middleware
 */
export const errorMiddleware = (
    error: Error,
    req: Request,
    res: Response,
    next: NextFunction
) => {
    console.error('Error:', error);

    if (error instanceof AppError) {
        res.status(error.statusCode).json({
            error: error.code,
            message: error.message,
        });
        return;
    }

    res.status(500).json({
        error: 'INTERNAL_ERROR',
        message: 'An unexpected error occurred',
    });
};
