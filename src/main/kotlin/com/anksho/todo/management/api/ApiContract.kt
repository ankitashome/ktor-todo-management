package com.anksho.todo.management.api

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Required
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class ApiLoginRequest(
    @Required
    val username: String,
    @Required
    val password: String
)

@Serializable
data class ApiLoginResponse(
    @Required
    val token: String
)

@Serializable
data class ApiTodoRequest(
    @Required
    val title: String,
    val description: String? = null,
    @Required
    @Serializable(with = ApiPrioritySerializer::class)
    val priority: ApiPriority
) {
    fun validate() {
        require(title.length in 3..100) { "Title must be between 3 and 100 characters." }
        require((description?.length ?: 0) <= 500) { "Description cannot exceed 500 characters." }
    }
}

@Serializable
data class ApiTodoResponse(
    val todos: List<ApiTodo>
)

@Serializable
data class ApiTodo(
    @Required
    val id: String,
    @Required
    val title: String,
    val description: String?,
    @Required
    @Serializable(with = ApiPrioritySerializer::class)
    val priority: ApiPriority,
    @Required
    val completed: Boolean
) {
    fun validate() {
        require(title.length in 3..100) { "Title must be between 3 and 100 characters." }
        require((description?.length ?: 0) <= 500) { "Description cannot exceed 500 characters." }
    }
}

@Serializable
enum class ApiPriority {
    UNKNOWN,
    LOW,
    MEDIUM,
    HIGH;

    companion object {
        fun fromName(name: String): ApiPriority = ApiPrioritySerializer.values[name] ?: UNKNOWN
    }
}

object ApiPrioritySerializer : KSerializer<ApiPriority> {
    internal val values: Map<String, ApiPriority> = mapOf(
        "LOW" to ApiPriority.LOW,
        "MEDIUM" to ApiPriority.MEDIUM,
        "HIGH" to ApiPriority.HIGH,
    )
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ApiPrioritySerializer", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: ApiPriority) = encoder.encodeString(value.name)
    override fun deserialize(decoder: Decoder) = values[decoder.decodeString()] ?: ApiPriority.UNKNOWN
}

@Serializable
data class ApiErrorResponse(
    @Required
    val errorCode: Int,
    val message: String? = null,
)