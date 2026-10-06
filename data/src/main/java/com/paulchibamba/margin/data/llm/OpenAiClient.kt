package com.paulchibamba.margin.data.llm

import com.paulchibamba.margin.data.io.readAtMost
import com.paulchibamba.margin.domain.llm.ApiKey
import com.paulchibamba.margin.domain.llm.LlmClient
import com.paulchibamba.margin.domain.llm.LlmExchange
import com.paulchibamba.margin.domain.llm.LlmFailure
import com.paulchibamba.margin.domain.llm.LlmFailureReason
import com.paulchibamba.margin.domain.llm.LlmModel
import com.paulchibamba.margin.domain.llm.LlmRequest
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URI
import java.net.URL
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OpenAiClient(
    private val open: (URL) -> HttpURLConnection = { url -> url.openConnection() as HttpURLConnection },
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : LlmClient {

    override suspend fun send(key: ApiKey, model: LlmModel, request: LlmRequest): LlmExchange =
        withContext(dispatcher) {
            val body = OpenAiRequestBody.of(model, request).toByteArray(Charsets.UTF_8)
            val first = attempt(key, body)
            val exchange = if (isWorthRetrying(first)) attempt(key, body) else first
            exchange.redacting(key)
        }

    private fun attempt(key: ApiKey, body: ByteArray): LlmExchange = try {
        post(key, body)
    } catch (_: SocketTimeoutException) {
        failed(LlmFailureReason.TIMED_OUT, "No reply within ${TIMEOUT_MILLIS / MILLIS_PER_SECOND} s")
    } catch (error: IOException) {
        failed(LlmFailureReason.OFFLINE, "${error.javaClass.simpleName}: ${error.message}")
    }

    private fun post(key: ApiKey, body: ByteArray): LlmExchange {
        val connection = open(ENDPOINT).apply { configure(key) }
        try {
            connection.outputStream.use { stream -> stream.write(body) }
            val code = connection.responseCode
            if (code == HttpURLConnection.HTTP_OK) return OpenAiResponseReader.read(textOf(connection.inputStream))
            return OpenAiErrorReader.failureOf(code, connection.errorStream?.let(::textOf))
        } finally {
            connection.disconnect()
        }
    }

    private fun HttpURLConnection.configure(key: ApiKey) {
        requestMethod = "POST"
        doOutput = true
        connectTimeout = TIMEOUT_MILLIS
        readTimeout = TIMEOUT_MILLIS
        instanceFollowRedirects = false
        useCaches = false
        setRequestProperty("Authorization", "Bearer ${key.value}")
        setRequestProperty("Content-Type", "application/json")
        setRequestProperty("Accept", "application/json")
    }

    private fun textOf(stream: InputStream): String =
        stream.use { body -> body.readAtMost(MAX_REPLY_BYTES).toString(Charsets.UTF_8) }

    private fun isWorthRetrying(exchange: LlmExchange): Boolean =
        exchange is LlmExchange.Failed && exchange.failure.reason in RETRIED

    private fun LlmExchange.redacting(key: ApiKey): LlmExchange = when (this) {
        is LlmExchange.Answered -> this
        is LlmExchange.Failed -> copy(failure = failure.copy(detail = SecretRedactor.redact(failure.detail, key)))
    }

    private fun failed(reason: LlmFailureReason, detail: String) = LlmExchange.Failed(LlmFailure(reason, detail))

    private companion object {
        val ENDPOINT: URL = URI("https://api.openai.com/v1/responses").toURL()
        val RETRIED = setOf(LlmFailureReason.SERVER_ERROR, LlmFailureReason.TIMED_OUT)
        const val TIMEOUT_MILLIS = 20_000
        const val MILLIS_PER_SECOND = 1_000
        const val MAX_REPLY_BYTES = 1024 * 1024
    }
}
