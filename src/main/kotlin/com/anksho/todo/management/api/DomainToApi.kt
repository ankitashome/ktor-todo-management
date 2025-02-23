package com.anksho.todo.management.api

import com.anksho.todo.management.domain.Priority
import com.anksho.todo.management.domain.Todo
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun Todo.toApi() = ApiTodo(
    id = id.toString(),
    title = title,
    description = description,
    priority = priority.toApi(),
    completed = completed
)

fun Priority.toApi() = when (this) {
    Priority.LOW -> ApiPriority.LOW
    Priority.MEDIUM -> ApiPriority.MEDIUM
    Priority.HIGH -> ApiPriority.HIGH
}