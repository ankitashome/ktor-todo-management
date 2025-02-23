@file:OptIn(ExperimentalUuidApi::class)

package com.anksho.todo.management.api

import com.anksho.todo.management.domain.Priority
import com.anksho.todo.management.domain.Todo
import io.ktor.server.plugins.BadRequestException
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

fun ApiTodoRequest.toDomain() = Todo(
    title = title,
    description = description,
    priority = priority.toDomain(),
)

fun ApiPriority.toDomain() = when (this) {
    ApiPriority.LOW -> Priority.LOW
    ApiPriority.MEDIUM -> Priority.MEDIUM
    ApiPriority.HIGH -> Priority.HIGH
    ApiPriority.UNKNOWN -> throw BadRequestException("Unknown TODO Priority specified, allowed values are: ${Priority.entries.map { it.name }}")
}

fun ApiTodo.toDomain() = Todo(
    id = Uuid.parse(id),
    title = title,
    description = description,
    priority = priority.toDomain(),
    completed = completed
)