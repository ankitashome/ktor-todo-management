package com.anksho.todo.management.api.routes

import com.anksho.todo.management.api.ApiErrorResponse
import com.anksho.todo.management.api.ApiTodo
import com.anksho.todo.management.api.ApiTodoRequest
import com.anksho.todo.management.api.ApiTodoResponse
import com.anksho.todo.management.api.toApi
import com.anksho.todo.management.api.toDomain
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
                val apiTodoRequest = call.receive<ApiTodoRequest>()
                apiTodoRequest.validate()
                call.respond(HttpStatusCode.Created, todoService.createTodo(apiTodoRequest.toDomain()).toApi())
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
                val apiTodo = call.receive<ApiTodo>()
                apiTodo.validate()
                val updatedTodo = todoService.updateTodo(id, apiTodo.toDomain())?.toApi()

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
