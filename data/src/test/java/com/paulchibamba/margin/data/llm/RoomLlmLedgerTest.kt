package com.paulchibamba.margin.data.llm

import com.paulchibamba.margin.data.database.DatabaseTest
import com.paulchibamba.margin.domain.llm.LlmCall
import com.paulchibamba.margin.domain.llm.LlmModel
import com.paulchibamba.margin.domain.llm.LlmPurpose
import com.paulchibamba.margin.domain.llm.LlmSpend
import com.paulchibamba.margin.domain.llm.LlmUsage
import com.paulchibamba.margin.domain.llm.MicroDollars
import java.time.Instant
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RoomLlmLedgerTest : DatabaseTest() {

    private val ledger by lazy { RoomLlmLedger(database) }
    private val startOfToday = Instant.parse("2026-10-06T00:00:00Z")

    @Test
    fun `every call is one row, failed ones included`() = runTest {
        ledger.record(call(at = startOfToday.plusSeconds(60), cost = 340))
        ledger.record(call(at = startOfToday.plusSeconds(120), cost = 0, error = "HTTP 503"))

        val rows = database.llmCallDao().all()
        assertEquals(2, rows.size)
        assertEquals(listOf(true, false), rows.map { it.ok })
        assertEquals("connection_test", rows.first().purpose)
        assertEquals("gpt-5-nano", rows.first().model)
        assertEquals(listOf(2_000L, 2_000L), rows.map { it.tokensIn })
        assertEquals("HTTP 503", rows.last().error)
    }

    @Test
    fun `spend counts only the calls since the start`() = runTest {
        ledger.record(call(at = startOfToday.minusSeconds(1), cost = 5_000))
        ledger.record(call(at = startOfToday, cost = 340))
        ledger.record(call(at = startOfToday.plusSeconds(60), cost = 60))

        assertEquals(LlmSpend(2, MicroDollars(400)), ledger.spendSince(startOfToday))
        assertEquals(LlmSpend(2, MicroDollars(400)), ledger.observeSpendSince(startOfToday).first())
    }

    @Test
    fun `an empty ledger has spent nothing`() = runTest {
        assertEquals(LlmSpend.NONE, ledger.spendSince(startOfToday))
    }

    private fun call(at: Instant, cost: Long, error: String? = null) = LlmCall(
        at = at,
        purpose = LlmPurpose.CONNECTION_TEST,
        model = LlmModel.GPT_5_NANO,
        usage = LlmUsage(tokensIn = 2_000, tokensCached = 0, tokensOut = 600),
        cost = MicroDollars(cost),
        isOk = error == null,
        error = error,
    )
}
