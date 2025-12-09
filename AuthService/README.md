# DoConnect Auth Service

Dedicated authentication microservice responsible for user registration, login, JWT issuance, refresh tokens, and token validation.

## Features

- BCrypt password hashing
- Access + refresh tokens (JWT HS256)
- Validation endpoint for downstream services via the API Gateway
- H2 in-memory database (swap to MySQL/Postgres by updating `application.yml`)
- Service discovery via Eureka

## Run locally

```powershell
cd AuthService
mvn spring-boot:run
```

Service listens on <http://localhost:8085> by default.

## API surface

| Method | Path             | Description                                                          |
| ------ | ---------------- | -------------------------------------------------------------------- |
| POST   | `/auth/register` | Register a new user, returns JWT tokens                              |
| POST   | `/auth/login`    | Login with email/password, returns tokens                            |
| POST   | `/auth/refresh`  | Exchange refresh token for a new access token                        |
| GET    | `/auth/validate` | Validate an existing access token (requires `Authorization: Bearer`) |

Configure the API Gateway to forward `/auth/**` directly without auth, while every other route requires a valid token issued by this service.
