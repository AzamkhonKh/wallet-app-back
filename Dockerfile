# Stage 1: Build the application using Maven and JDK
# FROM eclipse-temurin:17-jdk-jammy AS builder # OLD - JDK only
FROM maven:3.9-eclipse-temurin-17 AS builder
 # NEW - Includes Maven 3.9 and Temurin JDK 17

WORKDIR /workspace

# Copy pom.xml files first to leverage Docker cache for dependencies
COPY pom.xml .
COPY wallet-app-api/pom.xml ./wallet-app-api/
COPY wallet-app-auth/pom.xml ./wallet-app-auth/
COPY wallet-app-core/pom.xml ./wallet-app-core/
COPY wallet-app-common/pom.xml ./wallet-app-common/

# Download dependencies (Now 'mvn' command will exist)
RUN mvn -B dependency:resolve
# OR potentially better caching with go-offline:
# RUN mvn -B dependency:go-offline

# Copy source code
COPY wallet-app-api/src ./wallet-app-api/src
COPY wallet-app-auth/src ./wallet-app-auth/src
COPY wallet-app-core/src ./wallet-app-core/src
COPY wallet-app-common/src ./wallet-app-common/src

# Build the application JAR (skip tests during build)
RUN mvn -B package -DskipTests

# Stage 2: Create the final runtime image using JRE (Remains the same)
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# ... rest of stage 2 remains the same ...