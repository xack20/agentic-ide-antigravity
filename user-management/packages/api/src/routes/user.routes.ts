import { Router } from 'express';
import { container } from 'tsyringe';
import { UserController } from '../controllers/user.controller';
import { validate } from '../middleware/validation.middleware';
import { registerUserSchema } from '../validators/user.validator';

const router = Router();

// Resolve controller from DI container
const userController = container.resolve(UserController);

// Routes
router.post('/register', validate(registerUserSchema), userController.register);
router.get('/:id', userController.getById);

export const userRoutes = router;
