import 'reflect-metadata';
import express from 'express';
import dotenv from 'dotenv';
import { database } from '@user-management/infrastructure';
import { configureContainer } from './container';
import { errorMiddleware } from './middleware';

// Load environment variables
dotenv.config();

// Configure DI BEFORE importing routes (routes resolve from container)
configureContainer();

// Now import routes after container is configured
import { userRoutes, roleRoutes } from './routes';

// Import role service for seeding
import { container } from 'tsyringe';
import { RoleService } from '@user-management/core';

const app = express();
const PORT = process.env.PORT || 3000;
const MONGO_URI = process.env.MONGO_URI || 'mongodb://localhost:27017/user-management';

// Middleware
app.use(express.json());

// Routes
app.use('/api/users', userRoutes);
app.use('/api/roles', roleRoutes);

// Health check
app.get('/health', (req, res) => {
    res.json({ status: 'ok' });
});

// Error handling
app.use(errorMiddleware);

// Start server
async function start() {
    try {
        // Connect to MongoDB
        await database.connect(MONGO_URI);

        // Seed default roles
        const roleService = container.resolve<RoleService>('RoleService');
        await roleService.seedDefaultRoles();

        app.listen(PORT, () => {
            console.log(`Server running on http://localhost:${PORT}`);
        });
    } catch (error) {
        console.error('Failed to start server:', error);
        process.exit(1);
    }
}

start();
