package com.anksho.todo.management.services

import com.anksho.todo.management.domain.Todo
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import java.util.concurrent.ConcurrentHashMap

@OptIn(ExperimentalUuidApi::class)
class TodoService(meterRegistry: MeterRegistry) {
    private val todosStore: MutableMap<Uuid, Todo> = ConcurrentHashMap()

    // Metrics for tracking operations
    private val createdCounter: Counter = Counter.builder("todos.created")
        .description("Number of todos created")
        .register(meterRegistry)

    private val deletedCounter: Counter = Counter.builder("todos.deleted")
        .description("Number of todos deleted")
        .register(meterRegistry)

    private val creationTimer: Timer = Timer.builder("todos.creation.time")
        .description("Time taken to create a todo")
        .register(meterRegistry)

    private val updateTimer: Timer = Timer.builder("todos.update.time")
        .description("Time taken to update a todo")
        .register(meterRegistry)

    fun getAllTodos(): List<Todo> = todosStore.values.sortedWith(
        compareByDescending<Todo> { it.priority.ordinal }
            .thenBy { it.title.lowercase() }
    )

    fun getTodoById(id: String): Todo? = todosStore[Uuid.parse(id)]

    fun createTodo(todo: Todo): Todo {
        val added = creationTimer.recordCallable {
            todosStore.computeIfAbsent(todo.id) { todo }
        }
        createdCounter.increment()
        return added!!
    }

    fun updateTodo(id: String, updatedTodo: Todo): Todo? {
        return updateTimer.recordCallable {
            todosStore.computeIfPresent(Uuid.parse(id)) { _, _ -> updatedTodo.copy(id = Uuid.parse(id)) }
        }
    }

    fun deleteTodo(id: String): Boolean {
        val removed = todosStore.remove(Uuid.parse(id)) != null
        if (removed) {
            deletedCounter.increment()
        }
        return removed
    }
}