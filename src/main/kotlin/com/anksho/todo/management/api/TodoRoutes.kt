package com.anksho.todo.management.api

import com.anksho.todo.management.services.TodoService
import io.ktor.http.HttpStatusCode
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.server.auth.authenticate
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route

fun Route.todoRoutes(todoService: TodoService) {
    authenticate("auth-jwt") {
        route("/api/v1/todos") {
            // Get all todos
            get {
                val todos = ApiTodoResponse(todos = todoService.getAllTodos().map { it.toApi() })
                call.respond(HttpStatusCode.OK, todos)
            }

            //Create a new to-do
            post {
                val todoRequest = call.receive<ApiTodoRequest>().toDomain()
                call.respond(HttpStatusCode.Created, todoService.createTodo(todoRequest).toApi())
            }

            //Get a single to-do by id
            get("/{id}") {
                val id = call.parameters["id"] ?: throw BadRequestException("TODO id is required")
                val todo = todoService.getTodoById(id)?.toApi()

                todo?.let { call.respond(HttpStatusCode.OK, todo) }
                    ?: call.respond(
                        NotFound,
                        ApiErrorResponse(errorCode = NotFound.value, message = "TODO with id: $id not found")
                    )
            }

            //Update(full) an existing to-do
            put("/{id}") {
                val id = call.parameters["id"] ?: throw BadRequestException("TODO id is required")
                val toBeUpdatedTodo = call.receive<ApiTodo>().toDomain()
                val updatedTodo = todoService.updateTodo(id, toBeUpdatedTodo)?.toApi()

                updatedTodo?.let { call.respond(HttpStatusCode.OK, updatedTodo) } ?: call.respond(
                    NotFound,
                    ApiErrorResponse(errorCode = NotFound.value, message = "TODO with id: $id not found")
                )
            }

            //Delete a to-do
            delete("/{id}") {
                val id = call.parameters["id"] ?: throw BadRequestException("TODO id is required")
                if (todoService.deleteTodo(id)) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(
                        NotFound,
                        ApiErrorResponse(errorCode = NotFound.value, message = "TODO with id: $id not found")
                    )
                }
            }
        }
    }
}
