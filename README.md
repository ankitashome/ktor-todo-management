Here are the **cURL** commands to interact with the **TODO API**:

---

## **1️⃣ Create a New TODO (POST)**
```sh
curl -X POST http://localhost:8080/api/v1/todos \
     -H "Content-Type: application/json" \
     -d '{
           "title": "Buy groceries",
           "description": "Milk, eggs, and bread",
           "completed": false
         }'
```
📌 **Response:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "title": "Buy groceries",
  "description": "Milk, eggs, and bread",
  "completed": false
}
```
💡 The **server generates a unique `id`** (UUID) for the new TODO.

---

## **2️⃣ Get All TODOs (GET)**
```sh
curl -X GET http://localhost:8080/api/v1/todos
```
📌 **Response:**
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "title": "Buy groceries",
    "description": "Milk, eggs, and bread",
    "completed": false
  }
]
```

---

## **3️⃣ Get a Specific TODO by ID (GET)**
```sh
curl -X GET http://localhost:8080/api/v1/todos/550e8400-e29b-41d4-a716-446655440000
```
📌 **Response:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "title": "Buy groceries",
  "description": "Milk, eggs, and bread",
  "completed": false
}
```

---

## **4️⃣ Update a TODO (PUT)**
```sh
curl -X PUT http://localhost:8080/api/v1/todos/550e8400-e29b-41d4-a716-446655440000 \
     -H "Content-Type: application/json" \
     -d '{
           "title": "Buy groceries",
           "description": "Milk, eggs, bread, and butter",
           "completed": true
         }'
```
📌 **Response:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "title": "Buy groceries",
  "description": "Milk, eggs, bread, and butter",
  "completed": true
}
```
💡 **The `id` remains the same**, but the description and `completed` status are updated.

---

## **5️⃣ Delete a TODO (DELETE)**
```sh
curl -X DELETE http://localhost:8080/api/v1/todos/550e8400-e29b-41d4-a716-446655440000
```
📌 **Response:**
- **If deleted successfully:** `204 No Content`
- **If not found:** `404 Not Found`

---

## **6️⃣ Attempt to Get a Deleted TODO**
```sh
curl -X GET http://localhost:8080/api/v1/todos/550e8400-e29b-41d4-a716-446655440000
```
📌 **Response:**
```json
{
  "error": "TODO not found"
}
```
(With a `404 Not Found` status)

---

## **Summary of Endpoints**
| Method | Endpoint | Description |
|--------|---------|-------------|
| `POST` | `/api/v1/todos` | Create a new TODO |
| `GET` | `/api/v1/todos` | Get all TODOs |
| `GET` | `/api/v1/todos/{id}` | Get a specific TODO by ID |
| `PUT` | `/api/v1/todos/{id}` | Update an existing TODO |
| `DELETE` | `/api/v1/todos/{id}` | Delete a TODO |

Would you like me to add authentication headers (JWT token) in the cURL requests as well? 🚀