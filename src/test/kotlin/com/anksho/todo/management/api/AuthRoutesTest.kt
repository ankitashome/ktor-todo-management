package com.anksho.todo.management.api

import com.anksho.todo.management.configureRouting
import com.anksho.todo.management.configureSerialization
import com.anksho.todo.management.modules.configureAuthentication
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json.Default.decodeFromString

class AuthRoutesTest : ShouldSpec({
    should("register new user") {
        testApplication {
            application {
                configureSerialization()
                configureAuthentication()
                configureRouting()
            }

            val response = client.post("auth/v1/register") {
                contentType(ContentType.Application.Json)
                setBody("""{"username": "ankita", "password": "ValidP@ss123"}""")
            }

            response.status shouldBe HttpStatusCode.Created
            response.bodyAsText() shouldBe "User ankita registered successfully"
        }
    }

    should("not register existing user") {
        testApplication {
            application {
                configureSerialization()
                configureAuthentication()
                configureRouting()
            }

            val response = client.post("auth/v1/register") {
                contentType(ContentType.Application.Json)
                setBody("""{"username": "testuser", "password": "ValidP@ss123"}""")
            }

            response.status shouldBe HttpStatusCode.Conflict

            val errorResponse: ApiErrorResponse = decodeFromString(response.bodyAsText())
            errorResponse.errorCode shouldBe HttpStatusCode.Conflict.value
            errorResponse.message shouldBe "User testuser already exists"
        }
    }

    should("login registered user") {
        testApplication {
            application {
                configureSerialization()
                configureAuthentication()
                configureRouting()
            }

            //given: registered user
            client.post("auth/v1/register") {
                contentType(ContentType.Application.Json)
                setBody("""{"username": "ankita", "password": "ValidP@ss123"}""")
            }

            //when: login registered user
            val response = client.post("auth/v1/login") {
                contentType(ContentType.Application.Json)
                setBody("""{"username": "ankita", "password": "ValidP@ss123"}""")
            }

            //then
            response.status shouldBe HttpStatusCode.OK
            response.bodyAsText() shouldContain "token"
        }
    }

    should("not login with incorrect credentials") {
        testApplication {
            application {
                configureSerialization()
                configureAuthentication()
                configureRouting()
            }

            //given: registered user
            client.post("auth/v1/register") {
                contentType(ContentType.Application.Json)
                setBody("""{"username": "ankita", "password": "ValidP@ss123"}""")
            }

            //when: login registered user
            val response = client.post("auth/v1/login") {
                contentType(ContentType.Application.Json)
                setBody("""{"username": "ankita", "password": "wrongPass"}""")
            }

            //then
            response.status shouldBe HttpStatusCode.Unauthorized

            val errorResponse: ApiErrorResponse = decodeFromString(response.bodyAsText())
            errorResponse.errorCode shouldBe HttpStatusCode.Unauthorized.value
            errorResponse.message shouldBe "Incorrect username or password."

        }
    }
})