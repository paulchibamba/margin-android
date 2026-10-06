package com.paulchibamba.margin.domain.llm

import com.paulchibamba.margin.domain.repository.LlmLedger
import java.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeLlmLedger(calls: List<LlmCall> = emptyList()) : LlmLedger {
    private val recorded = MutableStateFlow(calls)

    val calls: List<LlmCall> get() = recorded.value

    override suspend fun record(call: LlmCall) {
        recorded.value += call
    }

    override suspend fun spendSince(start: Instant): LlmSpend = spendOf(recorded.value, start)

    override fun observeSpendSince(start: Instant): Flow<LlmSpend> = recorded.map { calls -> spendOf(calls, start) }

    private fun spendOf(calls: List<LlmCall>, start: Instant): LlmSpend {
        val since = calls.filter { call -> !call.at.isBefore(start) }
        return LlmSpend(since.size, since.fold(MicroDollars.ZERO) { total, call -> total + call.cost })
    }
}
