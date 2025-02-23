package com.anksho.todo.management.domain

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class Todo(
    val id: Uuid = Uuid.random(),
    val title: String,
    val description: String? = null,
    val priority: Priority,
    val completed: Boolean = false
)

enum class Priority {
    LOW,
    MEDIUM,
    HIGH
}