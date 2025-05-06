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

* **Language:** Java 17 (or 21)
* **Framework:** Spring Boot 3.x (WebFlux or MVC)
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

* **Java JDK:** Version 17 or 21 (Check `pom.xml`)
* **Maven:** Version 3.6+
* **Docker:** Latest stable version
* **Docker Compose:** Latest stable version (usually included with Docker Desktop)

## Setup

1. **Clone the repository:**

    ```bash
    git clone <your-repository-url>
    cd wallet-app
    ```

2. **Configure Environment Variables:**
    * Copy the environment template:

        ```bash
        cp .env.example .env
        ```

        *(If you didn't create `.env.example`, copy `.env` directly if it exists as a template, BUT ensure the actual `.env` with secrets is in `.gitignore`)*
    * Edit the `.env` file and set your database credentials (`DB_USER`, `DB_PASSWORD`), JWT secret (`JWT_SECRET`), and other configurations as needed. **Use strong, unique values, especially for production secrets.**

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

* The application will be available at `http://<your-server-ip>:8080`.
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
Use code with caution.
```

**Explanation and Next Steps:**

1. **Environment Variables:** The `.env` file is central. Make sure it's correctly populated for local use and **NEVER** commit it to Git if it contains real secrets. For production, use environment variables injected by your deployment system or a secrets manager.
2. **Docker Build Context:** The `.dockerignore` file is important to keep your build context small and build times faster.
3. **Multi-Stage Build:** The `Dockerfile` builds the JAR in a temporary JDK image and then copies *only* the JAR and necessary resources (like Flyway migrations) into a smaller JRE image for the final stage.
4. **Docker Compose Overrides:** Using `-f docker-compose.yml -f docker-compose.dev.yml` (or `.prod.yml`) merges the files, with the latter file overriding settings in the base file. This keeps configurations DRY (Don't Repeat Yourself).
5. **Database Persistence:** The `wallet_db_data` named volume ensures your PostgreSQL data persists even if you remove and recreate the database container.
6. **Networking:** Services within the same `docker-compose.yml` file (and its overrides) are automatically placed on a shared network (`wallet-net` in this case), allowing the `app` container to reach the `db` container using its service name (`db`) as the hostname.
7. **Development Workflow:** For code changes in development with Docker, you'll typically need to rebuild the image (`docker-compose build app`) and restart the services (`docker-compose up -d`) unless you integrate live reload tools like Spring Boot DevTools configured for remote usage. Simple debugging via the exposed port `5005` is often sufficient.
8. **Production Deployment:** The `docker-compose.prod.yml` provides a basic production setup. For real-world deployments, consider:
    * A reverse proxy (Nginx, Traefik) in front of the application for TLS termination, load balancing, etc.
    * Proper secrets management (Vault, AWS Secrets Manager, etc.) instead of relying solely on `.env` files on the server.
    * Centralized logging and monitoring solutions.
