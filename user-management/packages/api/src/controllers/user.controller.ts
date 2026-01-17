import { Request, Response, NextFunction } from 'express';
import { inject, injectable } from 'tsyringe';
import { IUserService, CreateUserDto, UpdateUserDto } from '@user-management/shared';

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
}
