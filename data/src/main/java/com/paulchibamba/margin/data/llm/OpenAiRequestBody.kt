package com.paulchibamba.margin.data.llm

import com.paulchibamba.margin.domain.llm.LlmModel
import com.paulchibamba.margin.domain.llm.LlmRequest
import com.paulchibamba.margin.domain.llm.LlmSchema
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject

object OpenAiRequestBody {

    fun of(model: LlmModel, request: LlmRequest): String = buildJsonObject {
        put("model", model.id)
        put("instructions", request.instructions)
        put("input", request.input)
        put("max_output_tokens", request.maxOutputTokens)
        put("store", false)
        request.reasoningEffort?.let { effort -> putJsonObject("reasoning") { put("effort", effort.key) } }
        putJsonObject("text") { put("format", formatOf(request.schema)) }
    }.toString()

    private fun formatOf(schema: LlmSchema): JsonObject = buildJsonObject {
        put("type", "json_schema")
        put("name", schema.name)
        put("schema", Json.parseToJsonElement(schema.json))
        put("strict", true)
    }
}
