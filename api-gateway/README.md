# API Gateway (TaskFlow — Phase 4)

Single entry point on port 8080. Routes requests to the right service by path, looking the service up via Eureka instead of a hardcoded address.

## Run it

Start order matters:
1. `eureka-server` (port 8761)
2. `taskflow-user-service` (port 8082)
3. `taskflow-task-service` (port 8081)
4. `api-gateway` (port 8080) — this one

```
mvn spring-boot:run
```

## Try it

Instead of hitting services directly on 8081/8082, go through the gateway on 8080:

```
POST http://localhost:8080/api/auth/register   (routes to User Service)
GET  http://localhost:8080/api/tasks            (routes to Task Service)
```

Same bodies/behavior as before — only the port and the fact that Eureka is doing the routing changes. Compare: the response should be identical whether you hit `localhost:8081/api/tasks` directly or `localhost:8080/api/tasks` through the gateway.
