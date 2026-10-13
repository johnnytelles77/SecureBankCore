# SecureBankCore

SecureBankCore is a backend banking REST API built with Java 21 and Spring Boot.

The project simulates core banking operations such as user management, account management, deposits, withdrawals, transfers, and transaction history. It includes JWT-based authentication, role-based authorization, request validation, global exception handling, automated testing, OpenAPI documentation, and Dockerized local infrastructure with PostgreSQL.

## Features

- User registration and management
- Bank account creation and management
- Deposits and withdrawals
- Account-to-account transfers
- Transaction history
- JWT authentication
- Role-based authorization with ADMIN and CUSTOMER roles
- Account ownership validation
- Request validation
- Global exception handling
- DTO-based request and response models
- Swagger / OpenAPI documentation
- Unit testing
- Integration testing
- Docker and Docker Compose support
- PostgreSQL persistence

## Tech Stack

- Java 21
- Spring Boot 3
- Spring Web
- Spring Data JPA
- Spring Security
- JWT
- PostgreSQL
- H2
- Maven
- JUnit 5
- Mockito
- MockMvc
- Swagger / OpenAPI
- Docker
- Docker Compose
- Git
- GitHub Actions

## Architecture

SecureBankCore follows a layered backend architecture:

```text
Controller
   ↓
Service
   ↓
Repository
   ↓
Database
```
The application separates responsibilities across controllers, services, repositories, DTOs, entities, security components, and exception handlers.

## Security

SecureBankCore implements:
- JWT-based stateless authentication
- BCrypt password hashing
- Role-based authorization
- Protected API endpoints
- ADMIN and CUSTOMER roles
- Account ownership validation
- Environment-based JWT secret configuration.

Sensitive values such as JWT secrets and database credentials are managed through environment variables and are not committed to the repository.
  
## API Endpoints

### Authentication
```text
POST /auth/login
```
### Users
```text
POST   /users
GET    /users
GET    /users/{id}
PUT    /users/{id}
DELETE /users/{id}
```
### Accounts
```text
POST   /accounts
GET    /accounts
GET    /accounts/{id}
PATCH  /accounts/{id}/status
DELETE /accounts/{id}
```
### Transactions
```text
POST /transactions/deposit
POST /transactions/withdraw
POST /transactions/transfer
GET  /transactions/account/{id}
```

## Testing

The project includes both unit and integration tests.
Unit tests cover service-layer business logic using JUnit and Mockito.
Integration tests validate API behavior using Spring Boot Test and MockMvc.
Main tested areas include:
- Authentication
- User operations
- Account operations
- Deposits
- Withdrawals
- Transfers
- Transaction history
- Authorization
- Error responses

Run tests with:
```bash
mvn test
```

## Continuous Integration (CI)

SecureBankCore uses GitHub Actions to automate build verification and testing.

### CI Pipeline

The pipeline runs on GitHub-hosted Ubuntu runners and performs the following steps:

- Checkout Repository — Retrieves the source code.

- Set Up Java 21 — Configures Eclipse Temurin JDK 21.

- Build and Test — Executes mvn clean verify to compile the application and run automated tests.

### Workflow Configuration

- CI Platform: GitHub Actions

- Operating System: Ubuntu Linux

- Java Version: 21

- Build Tool: Apache Maven

- Workflow File: .github/workflows/ci.yml

The workflow runs automatically on pushes and pull requests targeting the main branch.

## Branch Protection

The main branch requires pull requests and successful CI checks before merging.

This helps prevent changes that fail automated verification from being integrated into the main branch.

## Running with Docker

### Prerequisites
- Docker
- Docker Compose

### 1. Clone the repository
```bash
git clone https://github.com/johnnytelles77/SecureBankCore.git
cd SecureBankCore
```
### 2. Create the environment file
```bash
cp .env.example .env
```
Edit .env and replace the example values with your own local credentials.
Example:
```env
JWT_SECRET=replace_with_your_secret
POSTGRES_DB=securebank
POSTGRES_USER=securebank_user
POSTGRES_PASSWORD=replace_with_your_password
```
### 3. Start the application
```bash
docker compose up --build
```
The API will be available at:
```text
http://localhost:8080
```
PostgreSQL runs internally on port 5432 and is exposed locally on:
```text
localhost:5433
```
### 4. Stop the application
```bash
docker compose down
```

## API Documentation

When the application is running, Swagger UI is available at:
```text
http://localhost:8080/swagger-ui/index.html
```

## Project Structure
```text
src/main/java/com/core/securebank
├── config
├── controller
├── dto
├── exception
├── model
├── repository
├── security
└── service
```

## Roadmap
### Completed
- GitHub Actions Continuous Integration (CI)
- Automated Maven build and testing
- Main branch protection and required CI checks

### Planned
- Continuous Deployment (CD)
- Improved application logging and observability
- Additional security hardening
- Production deployment
- Expanded integration test coverage