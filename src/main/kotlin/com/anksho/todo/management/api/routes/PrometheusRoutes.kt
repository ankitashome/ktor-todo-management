package com.anksho.todo.management.api.routes

import io.ktor.server.request.header
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry

class PrometheusRoutes(private val meterRegistry: PrometheusMeterRegistry) {
    fun mount(route: Route) = with(route) {
        get {
            val format = call.request.header("Accept") ?: "text/plain"
            call.respond(meterRegistry.scrape(format))
        }
    }
}
