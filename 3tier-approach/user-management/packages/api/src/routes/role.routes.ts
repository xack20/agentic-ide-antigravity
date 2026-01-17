import { Router } from 'express';
import { container } from 'tsyringe';
import { RoleController } from '../controllers/role.controller';

const router = Router();

// Resolve controller from DI container
const roleController = container.resolve(RoleController);

// Routes
router.get('/', roleController.getAll);
router.get('/:id', roleController.getById);

export const roleRoutes = router;
