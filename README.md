# Exploring Software Architecture and Design Patterns

This repository is a collection of projects, each demonstrating a different software architectural style, design pattern, or system design. The goal is to provide practical, hands-on examples of various approaches to building software.

## Architectural Designs

This section contains examples of different architectural patterns.

### 1. 3-Tier Architecture

A user management service built with a traditional 3-tier architecture.

*   **Technology Stack:**
    *   **Runtime:** Node.js + TypeScript
    *   **Framework:** Express.js
    *   **Database:** MongoDB + Mongoose
    *   **Validation:** Zod
    *   **DI Container:** tsyringe
    *   **Password Hashing:** bcrypt
    *   **Email:** Nodemailer

*   **Implemented Features:**
    *   User registration with email notification
    *   Update user profile
    *   Get user by ID
    *   Soft delete user
    *   Get all users with pagination
    *   Search users
    *   Change password
    *   Role management (Admin, User, Moderator)
    *   Restore soft-deleted user

*   **How to Run:**
    ```bash
    cd 3tier-approach/user-management
    npm install
    npm run build
    npm run dev
    ```
    The server will be running on `http://localhost:3000`.

### 2. Clean Architecture

A user management service built following the Clean Architecture principles.

*   **Technology Stack:**
    *   **Runtime:** Java
    *   **Framework:** Spring Boot 4
    *   **Database:** MongoDB
    *   **Build Tool:** Gradle

*   **Implemented Features:**
    *   User registration
    *   View user profile
    *   Update user profile
    *   Change password
    *   Deactivate/Activate user
    *   Role assignment
    *   Search/List users

*   **How to Run:**
    ```bash
    cd clean-architecture-approach
    ./gradlew bootRun
    ```
    The server will be running on `http://localhost:8080`.

---
*I will add more sections here as I add more design patterns and architectural examples.*