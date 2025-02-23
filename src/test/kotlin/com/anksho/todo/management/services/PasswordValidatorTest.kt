package com.anksho.todo.management.services


import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.server.plugins.BadRequestException

class PasswordValidatorTest : StringSpec({

    "Valid password should not throw an exception" {
        validatePassword("StrongP@ss123")
    }

    "Password should be at least 8 characters long" {
        val exception = shouldThrow<BadRequestException> {
            validatePassword("Sh@1")
        }
        exception.message shouldBe "Password should be minimum 8 characters long"
    }

    "Password should be maximum 20 characters long" {
        val exception = shouldThrow<BadRequestException> {
            validatePassword("ThisIsAVeryLongPassword@123")
        }
        exception.message shouldBe "Password should be maximum 20 characters long"
    }

    "Password should contain at least one uppercase letter" {
        val exception = shouldThrow<BadRequestException> {
            validatePassword("weakpass@1")
        }
        exception.message shouldBe "Password should contain at least one uppercase letter"
    }

    "Password should contain at least one lowercase letter" {
        val exception = shouldThrow<BadRequestException> {
            validatePassword("WEAKPASS@1")
        }
        exception.message shouldBe "Password should contain at least one lowercase letter"
    }

    "Password should contain at least one digit" {
        val exception = shouldThrow<BadRequestException> {
            validatePassword("NoNumber@Pass")
        }
        exception.message shouldBe "Password should contain at least one digit"
    }

    "Password should contain at least one special character" {
        val exception = shouldThrow<BadRequestException> {
            validatePassword("NoSpecial123")
        }
        exception.message shouldBe "Password should contain at least one special character"
    }
})