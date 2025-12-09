# DoConnect Eureka Server

This module hosts the Netflix Eureka discovery server for the DoConnect microservices.

## Prerequisites

- Java 17+
- Maven 3.9+

## Running locally

```powershell
cd EurekaServer
mvn spring-boot:run
```

The dashboard is available at <http://localhost:8761> once the server is running.

## Next steps

- Point each microservice to `http://localhost:8761/eureka/` via `eureka.client.service-url.defaultZone`.
- Ensure the API Gateway and downstream services register with this server for discovery and load balancing.
