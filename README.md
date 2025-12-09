# DoConnect Monorepo

This root-level guide explains how to boot the entire DoConnect stack with Docker. Every microservice ships with a multi-stage Dockerfile, and `docker-compose.yml` wires them together so you only need a single command to start Eureka, the backend services, and the static frontend.

## Prerequisites

- Docker Desktop 4.30+ with Compose V2 (or Docker Engine + `docker compose` CLI)
- At least 8 GB of RAM available for containers
- (Optional) `make` or PowerShell 7 if you want to script additional helpers

## 1. Configure secrets

Edit `.env` if you want to change the shared JWT signing key:

```env
JWT_SECRET=doconnect-local-secret
```

All services read this value so tokens issued by `auth-service` validate everywhere else.

## 2. Build & run everything

From the repo root run:

```powershell
# Windows PowerShell
cd C:\Users\sande\Downloads\final_project
docker compose up --build
```

Docker Compose will:

1. Build every microservice (Maven multi-stage image) plus the Nginx-based frontend
2. Start Eureka (`http://localhost:8761`) so the other services can register
3. Launch Auth, Question, Answer, and User services on ports 8085/8083/8082/8084
4. Launch the Spring Cloud API Gateway on <http://localhost:8080>
5. Serve the frontend on <http://localhost:8088>

Logs for each container stream into the terminal. Press `Ctrl+C` to stop the stack, or run `docker compose down` from another shell.

## 3. Smoke test the deployment

Once `docker compose up` reports that all services are `UP`, verify quickly:

```powershell
# Check Eureka dashboard
start http://localhost:8761

# API Gateway health
curl http://localhost:8080/actuator/health

# Frontend
start http://localhost:8088
```

Use the UI to register a user, sign in, and post a question. All requests flow through the gateway to the appropriate microservice automatically.

## 4. Useful one-liners

```powershell
# Rebuild a single service without touching the rest
docker compose build question-service

# Inspect logs for troubleshooting
docker compose logs -f api-gateway

# Tear everything down (removes containers and network)
docker compose down -v
```

## 5. Local development tips

- When you change Java code, re-run `docker compose up --build` (or `docker compose build <service>` followed by `docker compose up -d <service>`) to rebuild only the affected container.
- Override per-service ports or JVM options by editing `docker-compose.yml` and setting `SERVER_PORT` / `JAVA_OPTS` environment variables.
- Run the traditional Maven workflows (`mvn test`, `mvn spring-boot:run`, etc.) outside Docker whenever you need the fastest feedback loop.
