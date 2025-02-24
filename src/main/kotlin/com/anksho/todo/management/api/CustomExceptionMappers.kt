package com.anksho.todo.management.api

import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import io.ktor.server.plugins.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.serialization.SerializationException
import org.slf4j.LoggerFactory

private val log = LoggerFactory.getLogger("com.anksho.todo.management.ExceptionMapper")

internal fun StatusPagesConfig.customExceptionMappers() {
    exception<BadRequestException> { call, e ->
        log.info("Bad request, url {}, msg: {}", call.request.uri, e.message)
        call.respond(BadRequest, ApiErrorResponse(BadRequest.value, e.message))
    }
    exception<SerializationException> { call, e ->
        log.info("Serialization error, url {}, msg: {}", call.request.uri, e.message)
        call.respond(BadRequest, ApiErrorResponse(BadRequest.value, e.message))
    }
    exception<IllegalArgumentException> { call, e ->
        log.info("Bad request, url {}, msg: {}", call.request.uri, e.message)
        call.respond(BadRequest, ApiErrorResponse(BadRequest.value, e.message))
    }
    exception<Throwable> { call, e ->
        log.error("Unhandled exception caught, url {}", call.request.uri, e)
        call.respond(
            HttpStatusCode.ServiceUnavailable,
            ApiErrorResponse(HttpStatusCode.ServiceUnavailable.value, "TODO Management is not available.")
        )
    }

    status(Unauthorized) {
        call.respond(
            Unauthorized,
            ApiErrorResponse(Unauthorized.value, "Invalid/expired token supplied.")
        )
    }
}