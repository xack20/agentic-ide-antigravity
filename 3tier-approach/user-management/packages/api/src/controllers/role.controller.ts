import { Request, Response, NextFunction } from 'express';
import { inject, injectable } from 'tsyringe';
import { RoleService } from '@user-management/core';

/**
 * Role controller handling HTTP requests
 */
@injectable()
export class RoleController {
    constructor(
        @inject('RoleService') private readonly roleService: RoleService
    ) { }

    /**
     * GET /api/roles
     */
    getAll = async (req: Request, res: Response, next: NextFunction) => {
        try {
            const roles = await this.roleService.getAllRoles();
            res.json(roles);
        } catch (error) {
            next(error);
        }
    };

    /**
     * GET /api/roles/:id
     */
    getById = async (req: Request, res: Response, next: NextFunction) => {
        try {
            const { id } = req.params;
            const role = await this.roleService.findById(id);

            if (!role) {
                res.status(404).json({ error: 'NOT_FOUND', message: 'Role not found' });
                return;
            }

            res.json(role);
        } catch (error) {
            next(error);
        }
    };
}
