# TODO Management API

## Overview

The TODO Management API is a simple RESTful service built using Ktor that allows users to manage their tasks. It supports user authentication via JWT tokens.

## Tech Stack
* Kotlin (Ktor Framework)
* Maven (Build tool)
* JWT Authentication (Secure API access)
* Kotest (Unit testing)


## Authentication
* JWT-based authentication is used.
* Users must register and login to get an access token.
* The token must be included in consequent API calls via the `Authorization: Bearer <TOKEN>` header

Below are the **cURL** commands to interact with the **TODO Management API**:

---

## Authentication routes
### Register a new user
```sh
curl -X POST "http://localhost:8080/auth/v1/register" \
-H "Content-Type: application/json" \
-d '{"username": "user1", "password": "StrongPass123!"}'
```
Response:
```sh
User user1 registered successfully
```
---

### Login and get token
```sh
curl -X POST "http://localhost:8080/auth/v1/login" \
     -H "Content-Type: application/json" \
     -d '{"username": "user1", "password": "StrongPass123!"}'
```
Response:
```json
{
    "token": "eyJhbGJ1c2V..."
}
```
---

## TODO routes
### Create a New TODO (POST)
```sh
curl -X POST http://localhost:8080/api/v1/todos \
     -H "Authorization: Bearer <token>" \
     -H "Content-Type: application/json" \
     -d '{
           "title": "Buy groceries",
           "description": "Milk, eggs, and bread",
           "priority": "MEDIUM"
         }'
```
Response:
```json
{
  "id": "479affb4-96ac-45dc-8e75-a504fecab07e",
  "title": "Buy groceries",
  "description": "Milk, eggs, and bread",
  "priority": "MEDIUM",
  "completed": false

}
```
The **server generates a unique `id`** (UUID) for the new TODO.

---

### Get All TODOs (GET)
```sh
curl -X GET http://localhost:8080/api/v1/todos \
     -H "Authorization: Bearer <token>"
```
Response:
```json
{
  "todos": [
    {
      "id": "479affb4-96ac-45dc-8e75-a504fecab07e",
      "title": "Buy groceries",
      "description": "Milk, eggs, and bread",
      "priority": "MEDIUM",
      "completed": false
    }
  ]
}
```

---

### Get a Specific TODO by ID (GET)
```sh
curl -X GET http://localhost:8080/api/v1/todos/479affb4-96ac-45dc-8e75-a504fecab07e \
     -H "Authorization: Bearer <token>"
```
Response:
```json
{
  "id": "479affb4-96ac-45dc-8e75-a504fecab07e",
  "title": "Buy groceries",
  "description": "Milk, eggs, and bread",
  "priority": "MEDIUM",
  "completed": false
}
```

---

### Update a TODO (PUT)
```sh
curl -X PUT http://localhost:8080/api/v1/todos/479affb4-96ac-45dc-8e75-a504fecab07e \
     -H "Authorization: Bearer <token>" \
     -H "Content-Type: application/json" \
     -d '{
           "id": "479affb4-96ac-45dc-8e75-a504fecab07e",
           "title": "Buy groceries",
           "description": "Milk, eggs, bread, and butter",
           "priority": "MEDIUM",
           "completed": true
         }'
```
Response:
```json
{
  "id": "479affb4-96ac-45dc-8e75-a504fecab07e",
  "title": "Buy groceries",
  "description": "Milk, eggs, bread, and butter",
  "priority": "MEDIUM",
  "completed": true
}
```
The `id` remains the same, but the `description` and `completed` status are updated.

---

### Delete a TODO (DELETE)
```sh
curl -X DELETE http://localhost:8080/api/v1/todos/479affb4-96ac-45dc-8e75-a504fecab07e \
     -H "Authorization: Bearer <token>"
```
Response:
- **If deleted successfully:** `204 No Content`
- **If not found:** `404 Not Found`

---

## Running the Project
###  Prerequisites
- Install JDK 21
- Install Maven

### Build and Run
- mvn clean install
- java -jar target/todo-management-1.0-SNAPSHOT.jar

### Running tests
mvn test

## Future Enhancements
- Persistent storage
- Users and multitenancy
- Priority based TODO sorting
- Simple UI
- History of TODO item changes tracking

