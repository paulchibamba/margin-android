package com.paulchibamba.margin.domain.repository

import com.paulchibamba.margin.domain.llm.LlmCall
import com.paulchibamba.margin.domain.llm.LlmSpend
import java.time.Instant
import kotlinx.coroutines.flow.Flow

interface LlmLedger {
    suspend fun record(call: LlmCall)
    suspend fun spendSince(start: Instant): LlmSpend
    fun observeSpendSince(start: Instant): Flow<LlmSpend>
}
