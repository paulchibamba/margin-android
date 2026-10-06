package com.paulchibamba.margin.data.llm

import com.paulchibamba.margin.data.llm.ScriptedConnection.Reply
import com.paulchibamba.margin.domain.llm.ApiKey
import com.paulchibamba.margin.domain.llm.LlmExchange
import com.paulchibamba.margin.domain.llm.LlmFailureReason
import com.paulchibamba.margin.domain.llm.LlmModel
import com.paulchibamba.margin.domain.llm.LlmPurpose
import com.paulchibamba.margin.domain.llm.LlmRequest
import com.paulchibamba.margin.domain.llm.LlmSchema
import com.paulchibamba.margin.domain.llm.LlmUsage
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.URL
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Test

class OpenAiClientTest {

    private val connections = mutableListOf<ScriptedConnection>()

    @Test
    fun `it posts a strict JSON-schema request over https with the key as a bearer token`() = runTest {
        send(Reply.Status(200, answered("""{\"ok\":true}""")))

        val connection = connections.single()
        assertEquals(URL("https://api.openai.com/v1/responses"), connection.url)
        assertEquals("POST", connection.requestMethod)
        assertEquals(20_000, connection.readTimeout)
        assertEquals("Bearer $FAKE_KEY", connection.headers["Authorization"])
        val body = Json.parseToJsonElement(connection.body).jsonObject
        assertEquals("gpt-5-nano", body["model"]?.jsonPrimitive?.content)
        assertEquals("false", body["store"]?.jsonPrimitive?.content)
        val format = body["text"]?.jsonObject?.get("format")?.jsonObject
        assertEquals("json_schema", format?.get("type")?.jsonPrimitive?.content)
        assertEquals("true", format?.get("strict")?.jsonPrimitive?.content)
        assertEquals("object", format?.get("schema")?.jsonObject?.get("type")?.jsonPrimitive?.content)
    }

    @Test
    fun `an answer gives its text and token usage`() = runTest {
        val exchange = send(Reply.Status(200, answered("""{\"ok\":true}""")))

        assertEquals(LlmExchange.Answered("""{"ok":true}""", LlmUsage(120, 100, 30)), exchange)
    }

    @Test
    fun `a refusal is a failure`() = runTest {
        val refusal = """{"status":"completed","output":[{"type":"message","content":""" +
            """[{"type":"refusal","refusal":"No."}]}],"usage":{"input_tokens":5,"output_tokens":1}}"""

        val exchange = assertIs<LlmExchange.Failed>(send(Reply.Status(200, refusal)))

        assertEquals(LlmFailureReason.REFUSED, exchange.failure.reason)
        assertEquals(LlmUsage(5, 0, 1), exchange.usage)
    }

    @Test
    fun `an incomplete reply is a failure that still reports its usage`() = runTest {
        val incomplete = """{"status":"incomplete","incomplete_details":{"reason":"max_output_tokens"},""" +
            """"output":[],"usage":{"input_tokens":50,"output_tokens":1000}}"""

        val exchange = assertIs<LlmExchange.Failed>(send(Reply.Status(200, incomplete)))

        assertEquals(LlmFailureReason.INCOMPLETE, exchange.failure.reason)
        assertEquals(1_000, exchange.usage.tokensOut)
    }

    @Test
    fun `a rejected key is reported without echoing the key`() = runTest {
        val echo = """{"error":{"message":"Incorrect API key provided: $FAKE_KEY and sk-test-***WXYZ."}}"""

        val exchange = assertIs<LlmExchange.Failed>(send(Reply.Status(401, echo)))

        assertEquals(LlmFailureReason.KEY_REJECTED, exchange.failure.reason)
        assertFalse(exchange.failure.detail.contains(FAKE_KEY))
        assertFalse(exchange.failure.detail.contains("sk-test"))
        assertEquals(1, connections.size)
    }

    @Test
    fun `a network error never carries the key`() = runTest {
        val exchange = assertIs<LlmExchange.Failed>(send(Reply.Throws(IOException("Lost $FAKE_KEY"))))

        assertEquals(LlmFailureReason.OFFLINE, exchange.failure.reason)
        assertFalse(exchange.failure.detail.contains(FAKE_KEY))
    }

    @Test
    fun `a server error is retried once`() = runTest {
        val exchange = send(Reply.Status(503, "{}"), Reply.Status(200, answered("{}")))

        assertIs<LlmExchange.Answered>(exchange)
        assertEquals(2, connections.size)
    }

    @Test
    fun `a timeout is retried once and then given up`() = runTest {
        val timeout = Reply.Throws(SocketTimeoutException("Read timed out"))

        val exchange = assertIs<LlmExchange.Failed>(send(timeout, timeout, timeout))

        assertEquals(LlmFailureReason.TIMED_OUT, exchange.failure.reason)
        assertEquals(2, connections.size)
    }

    @Test
    fun `a bad request is not retried`() = runTest {
        val exchange = assertIs<LlmExchange.Failed>(send(Reply.Status(400, """{"error":{"message":"Bad"}}""")))

        assertEquals(LlmFailureReason.REQUEST_REJECTED, exchange.failure.reason)
        assertEquals("HTTP 400: Bad", exchange.failure.detail)
        assertEquals(1, connections.size)
    }

    @Test
    fun `a reply that isn't JSON is unreadable`() = runTest {
        val exchange = assertIs<LlmExchange.Failed>(send(Reply.Status(200, "<html>")))

        assertEquals(LlmFailureReason.UNREADABLE, exchange.failure.reason)
    }

    private suspend fun send(vararg replies: Reply): LlmExchange {
        val queue = ArrayDeque(replies.toList())
        val client = OpenAiClient(
            open = { url -> ScriptedConnection(url, queue.removeFirst()).also(connections::add) },
            dispatcher = Dispatchers.Unconfined,
        )
        return client.send(ApiKey(FAKE_KEY), LlmModel.GPT_5_NANO, request)
    }

    private fun answered(text: String): String =
        """{"status":"completed","output":[{"type":"reasoning","summary":[]},""" +
            """{"type":"message","content":[{"type":"output_text","text":"$text"}]}],""" +
            """"usage":{"input_tokens":120,"input_tokens_details":{"cached_tokens":100},"output_tokens":30}}"""

    private companion object {
        const val FAKE_KEY = "sk-test-0123456789abcdefWXYZ"

        val request = LlmRequest(
            purpose = LlmPurpose.CONNECTION_TEST,
            instructions = "Reply with ok.",
            input = "Hello",
            schema = LlmSchema("test", """{"type":"object","properties":{},"additionalProperties":false}"""),
            maxOutputTokens = 100,
        )
    }
}
