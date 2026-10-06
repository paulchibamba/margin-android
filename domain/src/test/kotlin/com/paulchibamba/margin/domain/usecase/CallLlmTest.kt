package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.llm.ApiKey
import com.paulchibamba.margin.domain.llm.FakeApiKeyStore
import com.paulchibamba.margin.domain.llm.FakeLlmClient
import com.paulchibamba.margin.domain.llm.FakeLlmLedger
import com.paulchibamba.margin.domain.llm.FakeLlmSettingsRepository
import com.paulchibamba.margin.domain.llm.LlmCall
import com.paulchibamba.margin.domain.llm.LlmExchange
import com.paulchibamba.margin.domain.llm.LlmFailure
import com.paulchibamba.margin.domain.llm.LlmFailureReason
import com.paulchibamba.margin.domain.llm.LlmModel
import com.paulchibamba.margin.domain.llm.LlmPurpose
import com.paulchibamba.margin.domain.llm.LlmReply
import com.paulchibamba.margin.domain.llm.LlmRequest
import com.paulchibamba.margin.domain.llm.LlmSchema
import com.paulchibamba.margin.domain.llm.LlmSettings
import com.paulchibamba.margin.domain.llm.LlmUsage
import com.paulchibamba.margin.domain.llm.MicroDollars
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class CallLlmTest {

    private val clock = FixedClock(Instant.parse("2026-10-06T12:00:00Z"))
    private val keys = FakeApiKeyStore(ApiKey("sk-test-key-1234"))
    private val settings = FakeLlmSettingsRepository()
    private val ledger = FakeLlmLedger()
    private val client = FakeLlmClient()
    private val callLlm = CallLlm(keys, settings, ledger, client, clock)

    @Test
    fun `without a key it reports templates only and makes no call`() = runTest {
        keys.clear()

        assertEquals(LlmReply.TemplatesOnly, callLlm(request))
        assertEquals(0, client.requests.size)
        assertTrue(ledger.calls.isEmpty())
    }

    @Test
    fun `once today's spend reaches the cap no call is made`() = runTest {
        ledger.record(spentAt(Instant.parse("2026-10-06T09:00:00Z"), LlmSettings.DEFAULT_DAILY_CAP))

        assertEquals(LlmReply.OverCap, callLlm(request))
        assertEquals(0, client.requests.size)
        assertEquals(1, ledger.calls.size)
    }

    @Test
    fun `spend from yesterday doesn't count against today's cap`() = runTest {
        ledger.record(spentAt(Instant.parse("2026-10-05T23:59:00Z"), MicroDollars(1_000_000)))

        callLlm(request)

        assertEquals(1, client.requests.size)
    }

    @Test
    fun `a lower cap stops calls sooner`() = runTest {
        settings.saveSettings(LlmSettings(dailyCap = MicroDollars(500)))
        ledger.record(spentAt(clock.now(), MicroDollars(500)))

        assertEquals(LlmReply.OverCap, callLlm(request))
    }

    @Test
    fun `an answered call is sent to the chosen model and written to the ledger with its cost`() = runTest {
        settings.saveSettings(LlmSettings(bakeModel = LlmModel.GPT_5_MINI))
        client.exchange = LlmExchange.Answered("""{"ok":true}""", LlmUsage(1_000, 0, 1_000))

        val reply = callLlm(request)

        assertEquals(LlmReply.Answered("""{"ok":true}""", LlmModel.GPT_5_MINI), reply)
        assertEquals(LlmModel.GPT_5_MINI, client.requests.single().first)
        val call = ledger.calls.single()
        assertEquals(clock.now(), call.at)
        assertEquals(LlmPurpose.CONNECTION_TEST, call.purpose)
        assertEquals(MicroDollars(2_250), call.cost)
        assertTrue(call.isOk)
    }

    @Test
    fun `a failed call is still written to the ledger`() = runTest {
        client.exchange = LlmExchange.Failed(LlmFailure(LlmFailureReason.SERVER_ERROR, "HTTP 503"))

        val reply = callLlm(request)

        assertEquals(LlmReply.Failed(LlmFailureReason.SERVER_ERROR), reply)
        val call = ledger.calls.single()
        assertFalse(call.isOk)
        assertEquals("HTTP 503", call.error)
        assertEquals(MicroDollars.ZERO, call.cost)
    }

    private fun spentAt(at: Instant, cost: MicroDollars) = LlmCall(
        at = at,
        purpose = LlmPurpose.BAKE,
        model = LlmModel.GPT_5_NANO,
        usage = LlmUsage.NONE,
        cost = cost,
        isOk = true,
        error = null,
    )

    private companion object {
        val request = LlmRequest(
            purpose = LlmPurpose.CONNECTION_TEST,
            instructions = "Reply with ok.",
            input = "Hello",
            schema = LlmSchema("test", "{}"),
            maxOutputTokens = 100,
        )
    }
}
