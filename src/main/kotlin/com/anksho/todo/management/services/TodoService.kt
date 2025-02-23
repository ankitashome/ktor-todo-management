package com.anksho.todo.management.services

import com.anksho.todo.management.domain.Todo
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import java.util.concurrent.ConcurrentHashMap

@OptIn(ExperimentalUuidApi::class)
class TodoService {
    private val todosStore: MutableMap<Uuid, Todo> = ConcurrentHashMap()

    fun getAllTodos(): List<Todo> = todosStore.values.toList()

    fun getTodoById(id: String): Todo? = todosStore[Uuid.parse(id)]

    fun createTodo(todo: Todo): Todo {
        return todosStore.computeIfAbsent(todo.id) { todo }
    }

    fun updateTodo(id: String, updatedTodo: Todo): Todo? {
        return todosStore.computeIfPresent(Uuid.parse(id)) { _, _ -> updatedTodo.copy(id = Uuid.parse(id)) }
    }

    fun deleteTodo(id: String): Boolean {
        return todosStore.remove(Uuid.parse(id)) != null
    }
}