# Task Service (TaskFlow — Phase 1)

A standalone Spring Boot REST API backed by MongoDB. No auth, no other services yet — that comes in later phases.

## Run it

```
mvn spring-boot:run
```

Make sure MongoDB is running locally on `27017` first (you already have this).

The app starts on **http://localhost:8081**.

## Run the tests

```
mvn test
```

## Try the API (Thunder Client / Postman)

**Create a task**
```
POST http://localhost:8081/api/tasks
Content-Type: application/json

{
  "title": "Set up TaskFlow",
  "description": "Get Phase 1 running end to end",
  "status": "TODO",
  "dueDate": "2026-08-20"
}
```

**Get all tasks**
```
GET http://localhost:8081/api/tasks
```

**Get one task**
```
GET http://localhost:8081/api/tasks/{id}
```

**Get tasks by status**
```
GET http://localhost:8081/api/tasks/status/TODO
```

**Update a task**
```
PUT http://localhost:8081/api/tasks/{id}
Content-Type: application/json

{
  "title": "Set up TaskFlow",
  "description": "Done!",
  "status": "DONE",
  "dueDate": "2026-08-20"
}
```

**Delete a task**
```
DELETE http://localhost:8081/api/tasks/{id}
```

## Verify in Mongo

Open MongoDB for VS Code or Compass, connect to `localhost:27017`, look for database `taskflow` → collection `tasks`.
