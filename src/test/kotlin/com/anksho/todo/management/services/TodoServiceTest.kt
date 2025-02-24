package com.anksho.todo.management.services

import com.anksho.todo.management.domain.Priority
import com.anksho.todo.management.domain.Todo
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import kotlin.uuid.ExperimentalUuidApi


@OptIn(ExperimentalUuidApi::class)
class TodoServiceTest : ShouldSpec({
    val todoService = TodoService(SimpleMeterRegistry())

    should("fetch sorted TODOs on priority alphabetically") {
        //given tasks
        val todos = listOf(
            Todo(title = "Z", priority = Priority.HIGH),
            Todo(title = "E", priority = Priority.LOW),
            Todo(title = "B", priority = Priority.LOW),
            Todo(title = "R", priority = Priority.MEDIUM),
            Todo(title = "A", priority = Priority.MEDIUM)
        )
        todos.map { todoService.createTodo(it) }

        //when
        val sortedTodos = todoService.getAllTodos().map { it.priority to it.title }

        //then
        val expectedSortedTodos = listOf(
            Priority.HIGH to "Z",
            Priority.MEDIUM to "A",
            Priority.MEDIUM to "R",
            Priority.LOW to "B",
            Priority.LOW to "E"
        )
        sortedTodos shouldBe expectedSortedTodos
    }

    should("create a new TODO") {
        //given
        val todo = Todo(title = "Test TODO", description = "Description", priority = Priority.LOW)

        //when
        val newTodo = todoService.createTodo(todo)

        //then
        with(newTodo) {
            title shouldBe "Test TODO"
            description shouldBe "Description"
            priority shouldBe Priority.LOW
            completed shouldBe false
        }
    }

    should("retrieve an existing TODO") {
        //given
        val todo = todoService.createTodo(
            Todo(
                title = "Fetch Test",
                description = "Description",
                priority = Priority.MEDIUM
            )
        )
        //when
        val retrieved = todoService.getTodoById(todo.id.toString())
        //then
        retrieved shouldBe todo
    }

    should("update an existing TODO") {
        //given
        val todo = todoService.createTodo(
            Todo(
                title = "Old Title",
                description = "Old Description",
                priority = Priority.HIGH
            )
        )
        //when
        val updated = todoService.updateTodo(todo.id.toString(), todo.copy(title = "New Title", completed = true))
        //then
        with(updated!!) {
            id shouldBe todo.id
            title shouldBe "New Title"
            description shouldBe "Old Description"
            priority shouldBe Priority.HIGH
            completed shouldBe true
        }
    }

    should("delete a TODO") {
        //given
        val todo =
            todoService.createTodo(Todo(title = "Delete Me", description = "Description", priority = Priority.LOW))
        //when
        val deleted = todoService.deleteTodo(todo.id.toString())
        //then
        deleted shouldBe true
        todoService.getTodoById(todo.id.toString()) shouldBe null
    }
})