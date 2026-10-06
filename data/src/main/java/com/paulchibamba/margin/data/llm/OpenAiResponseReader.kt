package com.paulchibamba.margin.data.llm

import com.paulchibamba.margin.domain.llm.LlmExchange
import com.paulchibamba.margin.domain.llm.LlmFailure
import com.paulchibamba.margin.domain.llm.LlmFailureReason
import com.paulchibamba.margin.domain.llm.LlmUsage
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

object OpenAiResponseReader {

    fun read(body: String): LlmExchange {
        val response = parse(body) ?: return failed(LlmFailureReason.UNREADABLE, "The reply isn't JSON")
        val usage = usageOf(response)
        return when (val status = response["status"].stringOrNull()) {
            COMPLETED -> answerOf(response, usage)
            INCOMPLETE -> failed(LlmFailureReason.INCOMPLETE, "Incomplete: ${incompleteReason(response)}", usage)
            else -> failed(LlmFailureReason.UNREADABLE, "Status $status: ${errorMessage(response)}", usage)
        }
    }

    private fun parse(body: String): JsonObject? = try {
        Json.parseToJsonElement(body).objectOrNull()
    } catch (_: SerializationException) {
        null
    }

    private fun answerOf(response: JsonObject, usage: LlmUsage): LlmExchange {
        val content = messageContent(response)
        content.firstNotNullOfOrNull { part -> part.textOf(REFUSAL, REFUSAL) }
            ?.let { refusal -> return failed(LlmFailureReason.REFUSED, "Refused: $refusal", usage) }
        val text = content.firstNotNullOfOrNull { part -> part.textOf(OUTPUT_TEXT, TEXT) }
            ?: return failed(LlmFailureReason.UNREADABLE, "The reply has no text", usage)
        return LlmExchange.Answered(text, usage)
    }

    private fun messageContent(response: JsonObject): List<JsonElement> = response["output"].arrayOrEmpty()
        .mapNotNull { item -> item.objectOrNull()?.takeIf { it["type"].stringOrNull() == MESSAGE } }
        .flatMap { message -> message["content"].arrayOrEmpty() }

    private fun JsonElement.textOf(type: String, field: String): String? =
        objectOrNull()?.takeIf { it["type"].stringOrNull() == type }?.get(field).stringOrNull()

    private fun usageOf(response: JsonObject): LlmUsage {
        val usage = response["usage"].objectOrNull() ?: return LlmUsage.NONE
        return LlmUsage(
            tokensIn = usage["input_tokens"].longOrZero(),
            tokensCached = usage["input_tokens_details"].objectOrNull()?.get("cached_tokens").longOrZero(),
            tokensOut = usage["output_tokens"].longOrZero(),
        )
    }

    private fun incompleteReason(response: JsonObject): String? =
        response["incomplete_details"].objectOrNull()?.get("reason").stringOrNull()

    private fun errorMessage(response: JsonObject): String? =
        response["error"].objectOrNull()?.get("message").stringOrNull()

    private fun failed(reason: LlmFailureReason, detail: String, usage: LlmUsage = LlmUsage.NONE) =
        LlmExchange.Failed(LlmFailure(reason, detail), usage)

    private const val COMPLETED = "completed"
    private const val INCOMPLETE = "incomplete"
    private const val MESSAGE = "message"
    private const val OUTPUT_TEXT = "output_text"
    private const val TEXT = "text"
    private const val REFUSAL = "refusal"
}
