# User Service (TaskFlow — Phase 2)

Standalone Spring Boot app: registration/login with PostgreSQL storage, BCrypt password hashing, and JWT issuance.

## 1. Start PostgreSQL (via Docker)

You don't need to install Postgres natively — run it in a container:

```
docker run --name taskflow-postgres ^
  -e POSTGRES_USER=postgres ^
  -e POSTGRES_PASSWORD=postgres ^
  -e POSTGRES_DB=taskflow_users ^
  -p 5432:5432 ^
  -d postgres:16
```

(The `^` line-continuation is for Windows CMD. In PowerShell, put it all on one line or use `` ` `` instead of `^`.)

Check it's running:
```
docker ps
```

To stop/start it later (data persists as long as you don't `docker rm` it):
```
docker stop taskflow-postgres
docker start taskflow-postgres
```

## 2. Run the app

```
mvn spring-boot:run
```

Starts on **http://localhost:8082**. On first run, Hibernate (`ddl-auto: update`) will auto-create the `users` table for you — no manual SQL needed.

## 3. Run the tests

```
mvn test
```

## 4. Try it in Thunder Client

**Register**
```
POST http://localhost:8082/api/auth/register
Content-Type: application/json

{
  "username": "alice",
  "email": "alice@example.com",
  "password": "supersecret123"
}
```
Returns a JWT token, e.g.:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "alice",
  "role": "USER"
}
```

**Login**
```
POST http://localhost:8082/api/auth/login
Content-Type: application/json

{
  "username": "alice",
  "password": "supersecret123"
}
```

**Try registering the same username again** — should get `409 Conflict`.

**Try logging in with the wrong password** — should get `401 Unauthorized`.

**Copy the token** from a register/login response — you'll use it in Phase 4 as a `Bearer` token when Task Service calls User Service to validate a user.
