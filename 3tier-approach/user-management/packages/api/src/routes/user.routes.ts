import { Router } from 'express';
import { container } from 'tsyringe';
import { UserController } from '../controllers/user.controller';
import { validate, validateQuery } from '../middleware/validation.middleware';
import {
    registerUserSchema,
    updateUserSchema,
    getAllUsersSchema,
    searchUsersSchema,
    changePasswordSchema
} from '../validators/user.validator';

const router = Router();

// Resolve controller from DI container
const userController = container.resolve(UserController);

// Routes
router.get('/search', validateQuery(searchUsersSchema), userController.search);
router.get('/', validateQuery(getAllUsersSchema), userController.getAll);
router.get('/:id', userController.getById);
router.post('/register', validate(registerUserSchema), userController.register);
router.put('/:id', validate(updateUserSchema), userController.update);
router.put('/:id/password', validate(changePasswordSchema), userController.changePassword);
router.delete('/:id', userController.softDelete);
router.post('/:id/restore', userController.restore);

export const userRoutes = router;
