package com.anksho.todo.management

import com.anksho.todo.management.api.authRoutes
import com.anksho.todo.management.api.customExceptionMappers
import com.anksho.todo.management.api.todoRoutes
import com.anksho.todo.management.modules.LifecycleModule.configureLifecycle
import com.anksho.todo.management.modules.configureAuthentication
import com.anksho.todo.management.modules.configureMonitoring
import com.anksho.todo.management.services.TodoService
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json

fun main() {
    embeddedServer(
        factory = Netty,
        port = 8080,
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    configureLifecycle()
    configureSerialization()
    configureAuthentication()
    configureRouting()
    configureStatusPages()
    configureMonitoring()
}

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
}

fun Application.configureRouting() {
    val todoService = TodoService()
    routing {
        authRoutes()
        todoRoutes(todoService)
    }
}

fun Application.configureStatusPages() {
    install(StatusPages) {
        customExceptionMappers()
    }
}