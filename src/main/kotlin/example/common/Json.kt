package example.common

import com.fasterxml.jackson.databind.ObjectMapper


object Json {
    val objectMapper = ObjectMapper()

    fun toJson(model: Any?): String = objectMapper.writeValueAsString(model)
}
