package com.anksho.todo.management.modules

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import java.util.*

private const val secret = "todosecretkey"
private const val issuer = "todo-management"
private const val audience = "todo-users"

fun Application.configureAuthentication() {
    install(Authentication) {
        jwt("auth-jwt") {
            realm = "Todo Management auth"
            verifier(
                JWT.require(Algorithm.HMAC256(secret))
                    .withIssuer(issuer)
                    .withAudience(audience)
                    .build()
            )
            validate { credential ->
                if (credential.payload.getClaim("username").asString() != null &&
                    credential.payload.expiresAt.time > System.currentTimeMillis()
                ) {
                    JWTPrincipal(credential.payload)
                } else null
            }
        }
    }
}

fun generateToken(username: String): String = JWT.create()
    .withIssuer(issuer)
    .withAudience(audience)
    .withClaim("username", username)
    .withExpiresAt(Date(System.currentTimeMillis() + 3600 * 1000)) // 1 hour expiry
    .sign(Algorithm.HMAC256(secret))

