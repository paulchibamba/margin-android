package com.paulchibamba.margin.data.llm

import com.paulchibamba.margin.domain.llm.LlmExchange
import com.paulchibamba.margin.domain.llm.LlmFailure
import com.paulchibamba.margin.domain.llm.LlmFailureReason
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

object OpenAiErrorReader {

    fun failureOf(code: Int, body: String?): LlmExchange.Failed {
        val message = body?.let(::messageOf)
        val detail = if (message == null) "HTTP $code" else "HTTP $code: $message"
        return LlmExchange.Failed(LlmFailure(reasonOf(code), detail))
    }

    private fun reasonOf(code: Int): LlmFailureReason = when (code) {
        HTTP_UNAUTHORIZED, HTTP_FORBIDDEN -> LlmFailureReason.KEY_REJECTED
        HTTP_TOO_MANY_REQUESTS -> LlmFailureReason.RATE_LIMITED
        in SERVER_ERRORS -> LlmFailureReason.SERVER_ERROR
        else -> LlmFailureReason.REQUEST_REJECTED
    }

    private fun messageOf(body: String): String? = try {
        Json.parseToJsonElement(body).objectOrNull()?.get("error").objectOrNull()?.get("message").stringOrNull()
    } catch (_: SerializationException) {
        null
    }

    private const val HTTP_UNAUTHORIZED = 401
    private const val HTTP_FORBIDDEN = 403
    private const val HTTP_TOO_MANY_REQUESTS = 429
    private val SERVER_ERRORS = 500..599
}
