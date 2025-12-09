# DoConnect API Gateway

Spring Cloud Gateway instance that fronts all downstream DoConnect microservices. It registers with Eureka, enforces JWT authentication, and provides centralized routing / fallbacks.

## Run locally

```powershell
cd ApiGateway
mvn spring-boot:run
```

The gateway listens on <http://localhost:8080>.

## Notes

- Configure each microservice with a unique `spring.application.name` so it can be addressed via `lb://SERVICE-ID`.
- Update `GatewayRoutesConfig` once additional services (chat, engagement, notification, etc.) are available.
- JWT validation currently expects tokens signed with the shared secret defined under `security.jwt.secret`. Replace with a secure value in production.
