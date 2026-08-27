# Eureka Discovery Server (TaskFlow — Phase 3)

The service registry. Task Service and User Service will register themselves here on startup, and (later) look each other up through it instead of hardcoded URLs.

## Run it

```
mvn spring-boot:run
```

Starts on **http://localhost:8761**.

## See it working

Open **http://localhost:8761** in a browser — Eureka ships its own dashboard. You'll see an "Instances currently registered with Eureka" section. It's empty until you start Task Service and User Service with the client changes (see the main project README / chat instructions).

## Order matters

Start this **first**, before Task Service or User Service — they'll try to register with it on their own startup, and will just retry quietly in the background if it's not up yet, but it's cleaner to have it running first.
