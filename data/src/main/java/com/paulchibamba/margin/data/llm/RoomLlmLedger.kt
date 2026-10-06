package com.paulchibamba.margin.data.llm

import com.paulchibamba.margin.data.database.MarginDatabase
import com.paulchibamba.margin.domain.llm.LlmCall
import com.paulchibamba.margin.domain.llm.LlmSpend
import com.paulchibamba.margin.domain.repository.LlmLedger
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomLlmLedger @Inject constructor(private val database: MarginDatabase) : LlmLedger {
    private val dao get() = database.llmCallDao()

    override suspend fun record(call: LlmCall) {
        dao.insert(LlmCallMapper.toEntity(call))
    }

    override suspend fun spendSince(start: Instant): LlmSpend =
        LlmCallMapper.toSpend(dao.spendSince(start.toEpochMilli()))

    override fun observeSpendSince(start: Instant): Flow<LlmSpend> =
        dao.observeSpendSince(start.toEpochMilli()).map(LlmCallMapper::toSpend)
}
