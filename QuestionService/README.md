# Question Service

Question Service is a standalone Spring Boot microservice that manages all question-related operations for the DoConnect platform.

## Features

- Manage question CRUD operations with validation and role-based security
- JWT-protected endpoints that integrate with the Auth Service
- Eureka client registration for service discovery
- Exposes REST endpoints via Spring Web, with DTO mapping and validation
- Uses H2 by default for local development (switchable to PostgreSQL/MySQL)

## Tech Stack

- Spring Boot 3.3.x
- Spring Data JPA
- Spring Security with JWT
- Spring Cloud Netflix Eureka Client
- OpenAPI (SpringDoc)

## Running Locally

1. Ensure Eureka Server and Auth Service are running.
2. Set `JWT_SECRET` environment variable to match Auth Service.
3. From the `QuestionService` directory run:
   ```bash
   mvn spring-boot:run
   ```
4. Service registers as `question-service` in Eureka. Access via API Gateway routes.

## API Summary

| Method | Endpoint                 | Description      | Roles       |
| ------ | ------------------------ | ---------------- | ----------- |
| POST   | `/api/questions`         | Create question  | USER, ADMIN |
| GET    | `/api/questions/{id}`    | Fetch by ID      | USER, ADMIN |
| GET    | `/api/questions?topic=`  | Filter by topic  | USER, ADMIN |
| GET    | `/api/questions?userId=` | Filter by author | USER, ADMIN |
| GET    | `/api/questions`         | List all         | USER, ADMIN |
| PUT    | `/api/questions/{id}`    | Update           | USER, ADMIN |
| DELETE | `/api/questions/{id}`    | Delete           | ADMIN       |

## Configuration

Key settings in `src/main/resources/application.yml`:

- `spring.datasource.*`: database connection
- `jwt.*`: shared secret and metadata
- `eureka.client.*`: registry endpoint

## Testing

```bash
mvn test
```

## Next Steps

- Replace H2 with production database
- Add tag management endpoints once frontend supports tagging
- Emit domain events for feeds/notifications
