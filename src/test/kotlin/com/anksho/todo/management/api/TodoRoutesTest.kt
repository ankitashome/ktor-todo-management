package com.anksho.todo.management.api

import com.anksho.todo.management.configureRouting
import com.anksho.todo.management.configureSerialization
import com.anksho.todo.management.modules.configureAuthentication
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json.Default.decodeFromString
import java.util.*

class TodoRoutesTest : ShouldSpec({
    lateinit var token: String

    beforeTest {
        testApplication {
            application {
                configureSerialization()
                configureAuthentication()
                configureRouting()
            }

            client.post("auth/v1/register") {
                contentType(ContentType.Application.Json)
                setBody("""{"username": "todoUser", "password": "ValidP@ss123"}""")
            }
            val loginResponse = client.post("auth/v1/login") {
                contentType(ContentType.Application.Json)
                setBody("""{"username": "todoUser", "password": "ValidP@ss123"}""")
            }
            token = loginResponse.bodyAsText().substringAfter("token\": \"").substringBefore("\"")
        }
    }

    should("not access TODOs without auth") {
        testApplication {
            application {
                configureSerialization()
                configureAuthentication()
                configureRouting()
            }

            val response = client.get("/api/v1/todos")
            response.status shouldBe HttpStatusCode.Unauthorized
        }
    }

    should("retrieve TODOs") {
        testApplication {
            application {
                configureSerialization()
                configureAuthentication()
                configureRouting()
            }

            val response = client.get("/api/v1/todos") {
                header(HttpHeaders.Authorization, "Bearer $token")
            }
            response.status shouldBe HttpStatusCode.OK
        }
    }

    should("create a new TODO") {
        testApplication {
            application {
                configureSerialization()
                configureAuthentication()
                configureRouting()
            }

            val response = client.post("/api/v1/todos") {
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $token")
                setBody("""{"title": "Test TODO", "description": "Sample", "priority": "HIGH"}""")
            }
            response.status shouldBe HttpStatusCode.Created
            response.bodyAsText() shouldContain "Test TODO"
        }
    }

    should("retrieve a TODO by id and return 404 when unknown id") {
        testApplication {
            application {
                configureSerialization()
                configureAuthentication()
                configureRouting()
            }
            //given
            val createResponse: ApiTodo = decodeFromString(client.post("/api/v1/todos") {
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $token")
                setBody("""{"title": "Test TODO", "description": "Sample", "priority": "HIGH"}""")
            }.bodyAsText())

            //when
            val okResponse = client.get("/api/v1/todos/${createResponse.id}") {
                header(HttpHeaders.Authorization, "Bearer $token")
            }
            okResponse.status shouldBe HttpStatusCode.OK

            //when
            val id = UUID.randomUUID()
            val errorResponse = client.get("/api/v1/todos/$id") {
                header(HttpHeaders.Authorization, "Bearer $token")
            }
            errorResponse.status shouldBe HttpStatusCode.NotFound
        }
    }

    should("update an existing TODO and return 404 when unknown id") {
        testApplication {
            application {
                configureSerialization()
                configureAuthentication()
                configureRouting()
            }
            //given
            val createResponse = decodeFromString<ApiTodo>(client.post("/api/v1/todos") {
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $token")
                setBody("""{"title": "Test TODO", "description": "Sample", "priority": "HIGH"}""")
            }.bodyAsText())

            //when
            val response = client.put("/api/v1/todos/${createResponse.id}") {
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $token")
                setBody("""{"id": "${createResponse.id}","title": "Test TODO", "description": "Sample", "priority": "HIGH", "completed": true}""")
            }
            //then
            response.status shouldBe HttpStatusCode.OK
            val updatedTodo = decodeFromString<ApiTodo>(response.bodyAsText())
            updatedTodo shouldBe ApiTodo(createResponse.id, "Test TODO", "Sample", ApiPriority.HIGH, true)

            //when
            val id = UUID.randomUUID()
            val errorResponse = client.put("/api/v1/todos/$id") {
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $token")
                setBody("""{"id": "${createResponse.id}","title": "Test TODO", "description": "Sample", "priority": "HIGH", "completed": true}""")
            }
            errorResponse.status shouldBe HttpStatusCode.NotFound
        }
    }

    should("delete an existing TODO") {
        testApplication {
            application {
                configureSerialization()
                configureAuthentication()
                configureRouting()
            }
            //given
            val createResponse = decodeFromString<ApiTodo>(client.post("/api/v1/todos") {
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $token")
                setBody("""{"title": "Test TODO", "description": "Sample", "priority": "HIGH"}""")
            }.bodyAsText())

            //when
            val response = client.delete("/api/v1/todos/${createResponse.id}") {
                header(HttpHeaders.Authorization, "Bearer $token")
            }
            //then
            response.status shouldBe HttpStatusCode.NoContent

            //when
            val id = UUID.randomUUID()
            val errorResponse = client.delete("/api/v1/todos/$id") {
                header(HttpHeaders.Authorization, "Bearer $token")
            }
            errorResponse.status shouldBe HttpStatusCode.NotFound
        }
    }

    should("get 401 when using expired tokens") {
        fun generateExpiredToken(): String {
            val secretKey = "todosecretkey"

            return JWT.create()
                .withIssuer("todo-management")
                .withAudience("todo-users")
                .withClaim("username", "testuser")
                .withExpiresAt(Date(System.currentTimeMillis() - 1000)) // Expired 1 second ago
                .sign(Algorithm.HMAC256(secretKey))
        }

        testApplication {
            application {
                configureSerialization()
                configureAuthentication()
                configureRouting()
            }

            val expiredToken = generateExpiredToken()

            val response = client.get("api/v1/todos") {
                header(HttpHeaders.Authorization, "Bearer $expiredToken")
            }

            response.status shouldBe HttpStatusCode.Unauthorized
        }

    }
})