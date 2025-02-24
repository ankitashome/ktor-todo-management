package com.anksho.todo.management

import com.anksho.todo.management.api.customExceptionMappers
import com.anksho.todo.management.api.routes.PrometheusRoutes
import com.anksho.todo.management.api.routes.authRoutes
import com.anksho.todo.management.api.routes.todoRoutes
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
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
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
    val meterRegistry = configureMonitoring()
    configureRouting(meterRegistry)
    configureStatusPages()
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

fun Application.configureRouting(meterRegistry: MeterRegistry) {
    val todoService = TodoService(meterRegistry)
    routing {
        authRoutes()
        todoRoutes(todoService)
        route("/metrics") {
            PrometheusRoutes(meterRegistry as PrometheusMeterRegistry).mount(this)
        }

    }
}

fun Application.configureStatusPages() {
    install(StatusPages) {
        customExceptionMappers()
    }
}