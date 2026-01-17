# User Management Service

This repository contains a user management service built with a 3-tier architecture. It is structured as a monorepo with the following packages:

- `api`: Contains the API layer, responsible for handling HTTP requests and responses.
- `core`: Contains the business logic and application services.
- `infrastructure`: Contains data access logic, database connections, and external service integrations (e.g., email service).
- `shared`: Contains shared interfaces, types, and utility functions used across different layers.

## Setup

To set up the project, follow these steps:

1.  **Install dependencies**:
    ```bash
    npm install
    ```

2.  **Build the project**:
    ```bash
    npm run build
    ```

## Running the API

To run the API in development mode:

```bash
npm run dev
```

The API should then be accessible at `http://localhost:3000` (or the port configured in the `api` package).