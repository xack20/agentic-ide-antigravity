# eCommerce CQRS MVP

Java 21 + Spring Boot 3.x microservice-based distributed system using DDD, CQRS, Clean Architecture, RabbitMQ, and MongoDB.

## Architecture

Each subsystem consists of 4 separate deployable processes:
1. **Command API** (port 8081) - REST endpoints, validates & enqueues commands
2. **CommandHandler** (port 8082) - Consumes commands, executes domain logic
3. **EventHandler** (port 8083) - Consumes events, updates read projections
4. **Query API** (port 8084) - REST endpoints for read queries

## Quick Start

### Prerequisites
- Java 21
- Docker & Docker Compose
- Gradle 8.5+

### Run Infrastructure
```bash
docker-compose up -d mongodb rabbitmq
```

### Build
```bash
./gradlew build
```

### Run ProductCatalog Subsystem
```bash
# Terminal 1 - Command API
./gradlew :product-catalog:command-api:bootRun

# Terminal 2 - CommandHandler
./gradlew :product-catalog:command-handler:bootRun

# Terminal 3 - EventHandler
./gradlew :product-catalog:event-handler:bootRun

# Terminal 4 - Query API
./gradlew :product-catalog:query-api:bootRun
```

## API Examples

### Create Product
```bash
curl -X POST http://localhost:8081/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Sample Product",
    "description": "A great product",
    "price": 99.99,
    "currency": "USD",
    "sku": "PROD-001"
  }'
```

### Query Products
```bash
curl http://localhost:8084/api/v1/products
```

## Project Structure

```
ecommerce-cqrs/
├── shared/
│   ├── common/          # Base abstractions (AggregateRoot, Command, Event, etc.)
│   ├── messaging/       # RabbitMQ infrastructure
│   └── persistence/     # MongoDB infrastructure
├── product-catalog/
│   ├── domain/          # Product aggregate, events, value objects
│   ├── application/     # Commands, handlers, interfaces
│   ├── infrastructure/  # Repository, event publisher implementations
│   ├── command-api/     # REST endpoints for commands
│   ├── command-handler/ # Queue consumer for commands
│   ├── event-handler/   # Queue consumer for projections
│   └── query-api/       # REST endpoints for queries
└── docker-compose.yml
```

## Running Tests
```bash
./gradlew test
```
