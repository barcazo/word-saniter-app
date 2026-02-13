# Word Sanitizer App

The **Word Sanitizer App** is a Spring Boot-based microservice designed to protect communication by identifying and masking sensitive words in messages. It provides a high-performance sanitization engine supported by an internal management API and in-memory caching.

## Key Features

*   **Message Sanitization:** Mask sensitive words in text using an efficient `BreakIterator`-based approach.
*   **Sensitive Word Management:** Full CRUD API for managing the list of sensitive words.
*   **High Performance:** In-memory caching of sensitive words with periodic background refreshes from the database.
*   **Secure by Design:** Integration with **Keycloak** for OAuth2/JWT-based security.
*   **Modular Architecture:** Built with **Spring Modulith** to ensure maintainable and clean code structure.
*   **Auto-Documentation:** Integrated **Swagger UI** for easy API exploration.

## Tech Stack

*   **Language:** Java 21
*   **Framework:** Spring Boot 4.0.2, Spring Modulith
*   **Database:** Microsoft SQL Server (MSSQL)
*   **Migrations:** Liquibase
*   **Security:** Spring Security, Keycloak (OAuth2/JWT)
*   **Testing:** JUnit 5, Testcontainers, RestAssured
*   **API Docs:** SpringDoc OpenAPI (Swagger UI)
*   **Utilities:** Lombok, MapStruct

## Getting Started

### Prerequisites

*   [Podman](https://podman.io/getting-started) and Podman Compose
*   Java 21
*   Maven 3.x (or use the provided `mvnw`)

### Local Development

1.  **Start Infrastructure:**
    The application uses `spring-boot-docker-compose` (compatible with Podman) to automatically start MSSQL and Keycloak.
    ```bash
    podman-compose up
    ```

2.  **Run the Application:**
    During development, use the `local` or `dev` profile. The `dev` profile disables security for easier testing.
    ```bash
    ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
    ```
    Or via JAR:
    ```bash
    ./mvnw clean package
    java -Dspring.profiles.active=dev -jar target/word-sanitizer-app-0.0.1-SNAPSHOT.jar
    ```

3.  **Access the App:**
    *   **Application:** `http://localhost:8080`
    *   **Swagger UI:** `http://localhost:8080/swagger-ui.html`
    *   **Keycloak UI:** `http://localhost:8085/admin/master/console/#/SensitiveWordAPI`

## API Overview

### External API (Sanitization)
*   `POST /api/external/v1/sanitizer/sanitize`: Takes a message and returns the masked version.

### Internal API (Management)
*   `GET /api/internal/v1/sensitive-words`: Paginated list of words.
*   `POST /api/internal/v1/sensitive-words`: Add a new word.
*   `PUT /api/internal/v1/sensitive-words/{id}`: Update a word.
*   `DELETE /api/internal/v1/sensitive-words/{id}`: Remove a word.

## Security

In non-`dev` profiles, the application requires a valid JWT from Keycloak.
*   **Roles:**
    *   `ADMIN`: Full access to management APIs.
    *   `VIEWER`: Read-only access to management APIs.
*   The Sanitizer API requires a valid authenticated session.

## Testing

The project uses **Testcontainers** to run integration tests against real MSSQL and Keycloak instances.

```bash
./mvnw clean test
```

*   `BaseIT`: Abstract base for integration tests.
*   `ModularityTest`: Ensures Spring Modulith architectural constraints are met and generates documentation in `target/spring-modulith-docs`.

## Build & Podman Image

Create a container image using the Spring Boot Maven plugin:

```bash
./mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=za.co.brian/word-sanitizer-app
```

## Run the Image with Podman
```bash
podman run -p 8080:8080 za.co.brian/word-sanitizer-app
```

## Further Readings

*   [Spring Modulith](https://docs.spring.io/spring-modulith/reference/index.html)
*   [Keycloak Documentation](https://www.keycloak.org/documentation)
*   [Liquibase Documentation](https://docs.liquibase.com/home.html)
*   [Podman Documentation](https://podman.io/getting-started)
