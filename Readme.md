# Java Wallet App

A modern Java application for managing personal finances, including authentication, spaces for organization, transaction tracking, and offline synchronization capabilities.

## Features

* **Authentication:** Secure user login and registration (JWT-based).
* **Spaces:** Organize finances into different spaces (e.g., Personal, Vacation Fund).
* **Transactions:** Track income, expenses, and transfers within spaces.
* **Offline Synchronization:** Backend APIs designed to support client-side offline data sync.
* **API Documentation:** Integrated Swagger UI for API exploration.

## Architecture

This project follows a **Modular Monolith** architecture. Core functionalities (Auth, Core Wallet Logic) are separated into distinct Maven modules (`wallet-app-auth`, `wallet-app-core`), promoting code organization and maintainability, while still deploying as a single application unit initially (`wallet-app-api`).

## Technology Stack

* **Language:** Java 17
* **Framework:** Spring Boot 3.x (MVC)
* **Data Access:** Spring Data JPA (Hibernate), PostgreSQL
* **Authentication:** Spring Security 6.x, JWT
* **Database Migration:** Flyway
* **Build Tool:** Maven
* **API Docs:** Springdoc OpenAPI (Swagger UI)
* **Containerization:** Docker, Docker Compose
* **Utilities:** Lombok

## Project Structure

Use code with caution.
Markdown
wallet-app/
├── pom.xml # Parent POM
├── wallet-app-api/ # Main Application Runner, Controllers, DTOs, Security Config
├── wallet-app-auth/ # Authentication logic, User domain/repo/service
├── wallet-app-core/ # Core business logic (Spaces, Transactions), domain/repo/service
├── wallet-app-common/ # Shared utilities, base exceptions
├── .env # Local environment variables (GIT IGNORED!)
├── .dockerignore # Files to ignore during Docker build
├── Dockerfile # Defines how to build the application image
├── docker-compose.yml # Base Docker Compose configuration (DB, App)
├── docker-compose.dev.yml # Docker Compose overrides for Development
└── docker-compose.prod.yml # Docker Compose overrides for Production

## Prerequisites

* **Java JDK:** Version 17
* **Maven:** Version 3.6+
* **Docker:** Latest stable version
* **Docker Compose:** Latest stable version (usually included with Docker Desktop)

## Setup

1. **Clone the repository:**

    ```bash
    git clone https://github.com/AzamkhonKh/wallet-app-back.git
    cd wallet-app
    ```

2. **Configure Environment Variables:**
    * Copy the environment template:

        ```bash
        cp .env.example .env
        ```

## Running the Application

### 1. Local Development (using Maven)

This is useful for rapid development and debugging directly in your IDE.

* **Start Database:** You can use Docker Compose just for the database:

    ```bash
    # Make sure .env file has DB credentials
    docker-compose -f docker-compose.yml up -d db
    ```

* **Run Application:** Run the `WalletApplication.java` main method from your IDE, or use Maven:

    ```bash
    # Ensure SPRING_PROFILES_ACTIVE=dev is set (e.g., via IDE run config or OS env var)
    mvn spring-boot:run -pl wallet-app-api
    ```

* The application will be available at `http://localhost:8080` (or the configured `SERVER_PORT`).

### 2. Development (using Docker Compose)

This runs the application and database inside Docker containers, simulating a containerized environment.

* **Build and Start Containers:**

    ```bash
    # Ensure .env file is configured
    docker-compose -f docker-compose.yml -f docker-compose.dev.yml up --build -d
    ```

* The application will be available at `http://localhost:8080`.
* Debug port `5005` is exposed for remote debugging from your IDE.
* To stop: `docker-compose -f docker-compose.yml -f docker-compose.dev.yml down`

### 3. Production (using Docker Compose)

This runs the application using production settings. **Ensure your `.env` file or the deployment environment variables contain secure production secrets!**

* **Build and Start Containers:**

    ```bash
    # Ensure .env file/environment variables are set for production!
    docker-compose -f docker-compose.yml -f docker-compose.prod.yml up --build -d
    ```

* The application will be available at `http://<your-server-ip>:8080`
* To stop: `docker-compose -f docker-compose.yml -f docker-compose.prod.yml down`

## API Documentation

Once the application is running, Swagger UI is available at:

`http://localhost:8080/swagger-ui.html`

## Database Migrations

Database schema migrations are managed using **Flyway**. They are located in `wallet-app-core/src/main/resources/db/migration`. Migrations are applied automatically when the application starts.

## Testing

Run unit and integration tests using Maven:

```bash
mvn test
```