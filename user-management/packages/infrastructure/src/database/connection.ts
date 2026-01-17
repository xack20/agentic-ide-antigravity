import mongoose from 'mongoose';

/**
 * MongoDB connection manager
 */
export class Database {
    private static instance: Database;
    private isConnected = false;

    private constructor() { }

    static getInstance(): Database {
        if (!Database.instance) {
            Database.instance = new Database();
        }
        return Database.instance;
    }

    async connect(uri: string): Promise<void> {
        if (this.isConnected) {
            console.log('Already connected to MongoDB');
            return;
        }

        try {
            await mongoose.connect(uri);
            this.isConnected = true;
            console.log('Connected to MongoDB');
        } catch (error) {
            console.error('MongoDB connection error:', error);
            throw error;
        }
    }

    async disconnect(): Promise<void> {
        if (!this.isConnected) {
            return;
        }

        await mongoose.disconnect();
        this.isConnected = false;
        console.log('Disconnected from MongoDB');
    }
}

export const database = Database.getInstance();
