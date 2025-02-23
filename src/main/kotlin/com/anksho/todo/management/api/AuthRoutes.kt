package com.anksho.todo.management.api

import com.anksho.todo.management.modules.generateToken
import com.anksho.todo.management.services.validatePassword
import io.ktor.http.HttpStatusCode
import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.http.HttpStatusCode.Companion.Created
import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlin.collections.set

// In-memory user storage
val users = mutableMapOf("testuser" to "password123")

fun Route.authRoutes() {
    route("/auth/v1") {
        post("/login") {
            val request = call.receive<ApiLoginRequest>()

            users[request.username]
                ?.takeIf { it == request.password }
                ?.let {
                    val token = generateToken(request.username)
                    call.respond(HttpStatusCode.OK, ApiLoginResponse(token = token))
                } ?: call.respond(
                Unauthorized, ApiErrorResponse(Unauthorized.value, "Incorrect username or password.")
            )
        }

        post("/register") {
            val request = call.receive<ApiLoginRequest>()
            if (users.containsKey(request.username)) {
                call.respond(Conflict, ApiErrorResponse(Conflict.value, "User ${request.username} already exists"))
            }

            //validate password
            validatePassword(request.password)

            users[request.username] = request.password
            call.respondText("User ${request.username} registered successfully", status = Created)

        }
    }
}