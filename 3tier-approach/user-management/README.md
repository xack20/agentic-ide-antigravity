# User Management System

This project is a user management system built with a 3-tier architecture using Node.js, Express, and TypeScript. It provides a RESTful API for managing users and their roles.

## Architecture

The project is structured as a monorepo with the following packages:

-   `packages/api`: The presentation layer, responsible for handling HTTP requests, routing, and request validation. It uses Express and communicates with the core layer.
-   `packages/core`: The business logic layer, containing the application's services and core business rules.
-   `packages/infrastructure`: The data access and infrastructure layer, responsible for database interactions, repositories, and external services like email. It uses TypeORM for database communication.
-   `packages/shared`: Contains shared code used across all packages, such as interfaces, custom errors, and data transfer objects (DTOs).

## Getting Started

### Prerequisites

-   Node.js (v18 or higher)
-   npm (v9 or higher)
-   A running PostgreSQL instance

### Installation

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/your-username/your-repository.git
    cd your-repository/3tier-approach/user-management
    ```

2.  **Install dependencies:**
    ```bash
    npm install
    ```

3.  **Set up environment variables:**

    Navigate to the `packages/api` directory and create a `.env` file by copying the example:

    ```bash
    cp .env.example .env
    ```

    Update the `.env` file with your database connection details and other environment-specific settings.

### Running the Application

To start the development server, run the following command from the root of the `user-management` directory:

```bash
npm run dev
```

This will start the API server, and it will be accessible at `http://localhost:3000` by default.

## Available Scripts

The following scripts are available in the root `package.json`:

-   `npm run build`: Builds all the packages.
-   `npm run build:shared`: Builds the `shared` package.
-   `npm run build:infrastructure`: Builds the `infrastructure` package.
-   `npm run build:core`: Builds the `core` package.
-   `npm run build:api`: Builds the `api` package.
-   `npm run dev`: Starts the development server for the `api` package.

## API Endpoints

The following endpoints are available for user management:

| Method | Endpoint             | Description                       |
|--------|----------------------|-----------------------------------|
| GET    | `/users`             | Get a paginated list of all users |
| GET    | `/users/search`      | Search for users                  |
| GET    | `/users/:id`         | Get a user by their ID            |
| POST   | `/users/register`    | Register a new user               |
| PUT    | `/users/:id`         | Update a user's information       |
| PUT    | `/users/:id/password`| Change a user's password          |
| DELETE | `/users/:id`         | Soft delete a user                |
| POST   | `/users/:id/restore` | Restore a soft-deleted user       |
