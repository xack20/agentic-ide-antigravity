import { Request, Response, NextFunction } from 'express';
import { inject, injectable } from 'tsyringe';
import { IUserService, CreateUserDto, UpdateUserDto, ChangePasswordDto } from '@user-management/shared';
import { GetAllUsersQuery, SearchUsersQuery } from '../validators/user.validator';

/**
 * User controller handling HTTP requests
 */
@injectable()
export class UserController {
    constructor(
        @inject('IUserService') private readonly userService: IUserService
    ) { }

    /**
     * POST /api/users/register
     */
    register = async (req: Request, res: Response, next: NextFunction) => {
        try {
            const createUserDto: CreateUserDto = req.body;
            const user = await this.userService.register(createUserDto);
            res.status(201).json(user);
        } catch (error) {
            next(error);
        }
    };

    /**
     * GET /api/users/:id
     */
    getById = async (req: Request, res: Response, next: NextFunction) => {
        try {
            const { id } = req.params;
            const user = await this.userService.findById(id);

            if (!user) {
                res.status(404).json({ error: 'NOT_FOUND', message: 'User not found' });
                return;
            }

            res.json(user);
        } catch (error) {
            next(error);
        }
    };

    /**
     * PUT /api/users/:id
     */
    update = async (req: Request, res: Response, next: NextFunction) => {
        try {
            const { id } = req.params;
            const updateUserDto: UpdateUserDto = req.body;
            const user = await this.userService.update(id, updateUserDto);
            res.json(user);
        } catch (error) {
            next(error);
        }
    };

    /**
     * DELETE /api/users/:id (Soft Delete)
     */
    softDelete = async (req: Request, res: Response, next: NextFunction) => {
        try {
            const { id } = req.params;
            const success = await this.userService.softDelete(id);

            if (!success) {
                res.status(404).json({ error: 'NOT_FOUND', message: 'User not found or already deleted' });
                return;
            }

            res.status(204).send();
        } catch (error) {
            next(error);
        }
    };

    /**
     * POST /api/users/:id/restore
     */
    restore = async (req: Request, res: Response, next: NextFunction) => {
        try {
            const { id } = req.params;
            const user = await this.userService.restoreUser(id);
            res.json(user);
        } catch (error) {
            next(error);
        }
    };

    /**
     * PUT /api/users/:id/password
     */
    changePassword = async (req: Request, res: Response, next: NextFunction) => {
        try {
            const { id } = req.params;
            const data: ChangePasswordDto = req.body;
            await this.userService.changePassword(id, data);
            res.status(204).send();
        } catch (error) {
            next(error);
        }
    };

    /**
     * GET /api/users
     */
    getAll = async (req: Request, res: Response, next: NextFunction) => {
        try {
            const query = req.query as unknown as GetAllUsersQuery;
            const result = await this.userService.getAllUsers(
                {
                    page: query.page,
                    limit: query.limit,
                    sortBy: query.sortBy,
                    sortOrder: query.sortOrder,
                },
                query.includeDeleted
            );
            res.json(result);
        } catch (error) {
            next(error);
        }
    };

    /**
     * GET /api/users/search
     */
    search = async (req: Request, res: Response, next: NextFunction) => {
        try {
            const query = req.query as unknown as SearchUsersQuery;
            const result = await this.userService.searchUsers(
                {
                    q: query.q,
                    email: query.email,
                    phone: query.phone,
                    isActive: query.isActive,
                },
                {
                    page: query.page,
                    limit: query.limit,
                    sortBy: query.sortBy,
                    sortOrder: query.sortOrder,
                }
            );
            res.json(result);
        } catch (error) {
            next(error);
        }
    };
}
