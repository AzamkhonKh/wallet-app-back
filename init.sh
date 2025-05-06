#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

# --- Configuration ---
PROJECT_ROOT="wallet-app"
GROUP_ID="com.example.wallet"
ARTIFACT_ID_BASE="wallet-app"
VERSION="0.0.1-SNAPSHOT"
JAVA_VERSION="17" # Or 21
SPRING_BOOT_VERSION="3.2.0" # Use latest stable 3.x
SPRINGDOC_VERSION="2.3.0"

MODULES=("api" "auth" "core" "common")

# --- Helper Function for POM Header ---
write_pom_header() {
  local MODULE_ARTIFACT_ID=$1
  local MODULE_NAME=$2
  local MODULE_DESCRIPTION=$3
  local PACKAGING_TYPE=${4:-jar} # Default to jar

  cat <<EOF
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
EOF

  # Add parent section unless it's the root pom
  if [ "$PACKAGING_TYPE" != "pom" ]; then
    cat <<EOF
    <parent>
        <groupId>${GROUP_ID}</groupId>
        <artifactId>${ARTIFACT_ID_BASE}-parent</artifactId>
        <version>${VERSION}</version>
        <relativePath>../pom.xml</relativePath> <!-- Adjust path if needed -->
    </parent>
EOF
  fi

  # Add group/artifact/version/packaging for non-parent poms
  if [ "$PACKAGING_TYPE" != "pom" ]; then
    cat <<EOF
    <artifactId>${MODULE_ARTIFACT_ID}</artifactId>
EOF
  else
    # Parent POM specific settings
    cat <<EOF
    <!-- Use Spring Boot Starter Parent for managed dependency versions -->
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>${SPRING_BOOT_VERSION}</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>

    <groupId>${GROUP_ID}</groupId>
    <artifactId>${MODULE_ARTIFACT_ID}</artifactId>
    <version>${VERSION}</version>
    <packaging>${PACKAGING_TYPE}</packaging>
EOF
  fi

  cat <<EOF
    <name>${MODULE_NAME}</name>
    <description>${MODULE_DESCRIPTION}</description>

EOF
}

# --- Main Script ---
echo "Creating project structure for ${PROJECT_ROOT}..."

# Create root project directory
mkdir -p "${PROJECT_ROOT}"
cd "${PROJECT_ROOT}"
echo "Created root directory: ${PROJECT_ROOT}"

# --- Create Parent POM ---
echo "Creating parent pom.xml..."
PARENT_ARTIFACT_ID="${ARTIFACT_ID_BASE}-parent"
write_pom_header "${PARENT_ARTIFACT_ID}" "${ARTIFACT_ID_BASE}-parent" "Parent project for the Wallet App" "pom" > pom.xml

cat <<EOF >> pom.xml
    <modules>
EOF
for MODULE in "${MODULES[@]}"; do
  echo "        <module>${ARTIFACT_ID_BASE}-${MODULE}</module>" >> pom.xml
done
cat <<EOF >> pom.xml
    </modules>

    <properties>
        <java.version>${JAVA_VERSION}</java.version>
        <springdoc-openapi.version>${SPRINGDOC_VERSION}</springdoc-openapi.version>
        <!-- Define other common library versions here -->
        <jjwt.version>0.11.5</jjwt.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <!-- Manage versions of our own modules -->
EOF
for MODULE in "${MODULES[@]}"; do
cat <<EOF >> pom.xml
            <dependency>
                <groupId>\${project.groupId}</groupId>
                <artifactId>${ARTIFACT_ID_BASE}-${MODULE}</artifactId>
                <version>\${project.version}</version>
            </dependency>
EOF
done
cat <<EOF >> pom.xml

            <!-- Manage versions of external libraries -->
            <dependency>
                <groupId>org.springdoc</groupId>
                <artifactId>springdoc-openapi-starter-webflux-ui</artifactId> <!-- or webmvc -->
                <version>\${springdoc-openapi.version}</version>
            </dependency>
            <dependency>
                <groupId>org.springdoc</groupId>
                <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId> <!-- or webflux -->
                <version>\${springdoc-openapi.version}</version>
            </dependency>
            <dependency>
                <groupId>io.jsonwebtoken</groupId>
                <artifactId>jjwt-api</artifactId>
                <version>\${jjwt.version}</version>
            </dependency>
             <dependency>
                <groupId>io.jsonwebtoken</groupId>
                <artifactId>jjwt-impl</artifactId>
                <version>\${jjwt.version}</version>
                <scope>runtime</scope>
            </dependency>
            <dependency>
                <groupId>io.jsonwebtoken</groupId>
                <artifactId>jjwt-jackson</artifactId>
                <version>\${jjwt.version}</version>
                <scope>runtime</scope>
            </dependency>

            <!-- Add other managed dependencies here -->

        </dependencies>
    </dependencyManagement>

    <build>
        <pluginManagement> <!-- Manage plugin versions centrally -->
             <plugins>
                <plugin>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-maven-plugin</artifactId>
                    <!-- Version managed by spring-boot-starter-parent -->
                </plugin>
             </plugins>
        </pluginManagement>
    </build>

</project>
EOF

# --- Create Modules ---
PACKAGE_BASE_PATH=$(echo "${GROUP_ID}" | sed 's/\./\//g') # Converts com.example.wallet to com/example/wallet

for MODULE in "${MODULES[@]}"; do
  MODULE_DIR="${ARTIFACT_ID_BASE}-${MODULE}"
  MODULE_ARTIFACT_ID="${MODULE_DIR}"
  MODULE_PACKAGE_PATH="${PACKAGE_BASE_PATH}/${MODULE}"
  MODULE_DESCRIPTION="Module for ${MODULE} logic"

  echo "Creating module: ${MODULE_DIR}"
  mkdir -p "${MODULE_DIR}/src/main/java/${MODULE_PACKAGE_PATH}"
  mkdir -p "${MODULE_DIR}/src/main/resources"
  mkdir -p "${MODULE_DIR}/src/test/java/${MODULE_PACKAGE_PATH}"

  # Create module pom.xml
  write_pom_header "${MODULE_ARTIFACT_ID}" "${MODULE_DIR}" "${MODULE_DESCRIPTION}" "jar" > "${MODULE_DIR}/pom.xml"

  cat <<EOF >> "${MODULE_DIR}/pom.xml"
    <dependencies>
        <!-- Common Dependencies for most modules -->
         <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

        <!-- Module Specific Dependencies -->
EOF

  # Add specific dependencies based on module type
  case $MODULE in
    api)
      cat <<EOF >> "${MODULE_DIR}/pom.xml"
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-webflux</artifactId> <!-- Or web -->
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
         <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>

        <!-- Internal Module Dependencies -->
        <dependency>
            <groupId>\${project.groupId}</groupId>
            <artifactId>${ARTIFACT_ID_BASE}-auth</artifactId>
        </dependency>
        <dependency>
            <groupId>\${project.groupId}</groupId>
            <artifactId>${ARTIFACT_ID_BASE}-core</artifactId>
        </dependency>
         <dependency>
            <groupId>\${project.groupId}</groupId>
            <artifactId>${ARTIFACT_ID_BASE}-common</artifactId>
        </dependency>

        <!-- API Documentation (Choose one based on webflux/web) -->
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webflux-ui</artifactId> <!-- Managed by parent -->
        </dependency>
        <!--
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        </dependency>
        -->

        <!-- JWT Support -->
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
        </dependency>
         <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
        </dependency>

        <!-- Testing WebFlux/WebMVC -->
        <dependency>
            <groupId>io.projectreactor</groupId>
            <artifactId>reactor-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
            <scope>test</scope>
        </dependency>
EOF
      # Create main application class
      MAIN_APP_DIR="${MODULE_DIR}/src/main/java/${MODULE_PACKAGE_PATH}"
      mkdir -p "${MAIN_APP_DIR}/config"
      mkdir -p "${MAIN_APP_DIR}/controller"
      mkdir -p "${MAIN_APP_DIR}/dto"
      mkdir -p "${MAIN_APP_DIR}/security"

      cat <<EOF > "${MAIN_APP_DIR}/WalletApplication.java"
package ${GROUP_ID}.${MODULE};

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

// Scan components in dependent modules too
@SpringBootApplication(scanBasePackages = {"${GROUP_ID}.api", "${GROUP_ID}.core", "${GROUP_ID}.auth", "${GROUP_ID}.common"})
@ConfigurationPropertiesScan(basePackages = "${GROUP_ID}.api.config") // Example
public class WalletApplication {

    public static void main(String[] args) {
        SpringApplication.run(WalletApplication.class, args);
    }

}
EOF
      # Create application.yml
      cat <<EOF > "${MODULE_DIR}/src/main/resources/application.yml"
server:
  port: 8080

spring:
  application:
    name: ${ARTIFACT_ID_BASE}
  profiles:
    active: dev # Default profile

# Basic placeholder - configure DB, JPA, Security etc. here or in profile-specific files
logging:
  level:
    root: INFO
    ${GROUP_ID}: DEBUG

# Example placeholder for JWT config
app:
  jwt:
    secret: your-very-secret-key-replace-in-prod # CHANGE THIS! Use env vars or secrets manager
    issuer: my-wallet-app
    access-token-expiration-ms: 3600000 # 1 hour

# Springdoc OpenAPI basic config
springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html

---
spring:
  config:
    activate:
      on-profile: dev

# Dev specific config (e.g., H2 console, debug logging)

---
spring:
  config:
    activate:
      on-profile: prod

# Prod specific config (e.g., real DB connection from env vars)
# spring:
#   datasource:
#     url: \${DB_URL}
#     username: \${DB_USER}
#     password: \${DB_PASSWORD}
EOF
      # Create profile-specific files (empty for now)
      touch "${MODULE_DIR}/src/main/resources/application-dev.yml"
      touch "${MODULE_DIR}/src/main/resources/application-prod.yml"
      ;;
    auth | core)
      # Add common dependencies for domain modules like JPA
      cat <<EOF >> "${MODULE_DIR}/pom.xml"
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
         <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>\${project.groupId}</groupId>
            <artifactId>${ARTIFACT_ID_BASE}-common</artifactId>
        </dependency>
        <!-- Add DB driver e.g. PostgreSQL -->
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
        <!-- Add Flyway or Liquibase -->
         <dependency>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-core</artifactId>
        </dependency>
         <dependency>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-database-postgresql</artifactId> <!-- Add if using flyway >= 9.17 -->
        </dependency>
EOF
      # Create basic package structure inside domain modules
      DOMAIN_MODULE_DIR="${MODULE_DIR}/src/main/java/${MODULE_PACKAGE_PATH}"
      mkdir -p "${DOMAIN_MODULE_DIR}/config"
      mkdir -p "${DOMAIN_MODULE_DIR}/domain"
      mkdir -p "${DOMAIN_MODULE_DIR}/repository"
      mkdir -p "${DOMAIN_MODULE_DIR}/service"
      mkdir -p "${DOMAIN_MODULE_DIR}/exception"
      # Add db/migration for Flyway in one of the modules (e.g., core)
      if [ "$MODULE" == "core" ]; then
          mkdir -p "${MODULE_DIR}/src/main/resources/db/migration"
          touch "${MODULE_DIR}/src/main/resources/db/migration/V1__Initial_Schema.sql" # Placeholder migration
      fi
      ;;
    common)
      # Common usually has fewer dependencies, maybe just Lombok/validation if needed elsewhere
      cat <<EOF >> "${MODULE_DIR}/pom.xml"
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
            <optional>true</optional> <!-- Make optional if only needed by consumers -->
        </dependency>
EOF
      # Create basic package structure
      COMMON_MODULE_DIR="${MODULE_DIR}/src/main/java/${MODULE_PACKAGE_PATH}"
      mkdir -p "${COMMON_MODULE_DIR}/util"
      mkdir -p "${COMMON_MODULE_DIR}/exception"
      ;;
  esac

  # Close dependencies and add build section for modules needing it (e.g., api)
  cat <<EOF >> "${MODULE_DIR}/pom.xml"
    </dependencies>
EOF

  if [ "$MODULE" == "api" ]; then
  cat <<EOF >> "${MODULE_DIR}/pom.xml"

    <build>
        <plugins>
            <!-- Spring Boot Plugin to make executable JAR -->
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
EOF
  fi

  # Close project tag
  echo "</project>" >> "${MODULE_DIR}/pom.xml"

  echo "Created module structure and pom.xml for ${MODULE_DIR}"
done

echo "--------------------------------------------------"
echo "Project structure created successfully in '${PROJECT_ROOT}' directory."
echo "Next steps:"
echo "1. cd ${PROJECT_ROOT}"
echo "2. Review the generated pom.xml files and adjust dependencies."
echo "3. Add database migration scripts (e.g., in wallet-app-core/src/main/resources/db/migration)."
echo "4. Implement your application logic."
echo "5. Consider adding .gitignore file."
echo "6. Import the project into your IDE."
echo "--------------------------------------------------"

# Go back to original directory
cd ..

exit 0