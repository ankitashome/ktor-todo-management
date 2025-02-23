package com.anksho.todo.management.services

import io.ktor.server.plugins.BadRequestException

fun validatePassword(password: String) {
    if (password.length < 8) {
        throw BadRequestException("Password should be minimum 8 characters long")
    }
    if (password.length > 20) {
        throw BadRequestException("Password should be maximum 20 characters long")
    }
    if (!password.any { it.isUpperCase() }) {
        throw BadRequestException("Password should contain at least one uppercase letter")
    }
    if (!password.any { it.isLowerCase() }) {
        throw BadRequestException("Password should contain at least one lowercase letter")
    }
    if (!password.any { it.isDigit() }) {
        throw BadRequestException("Password should contain at least one digit")
    }
    if (!password.any { it in "!@#\$%^&*()-_+=<>?/{}[]" }) {
        throw BadRequestException("Password should contain at least one special character")
    }
}