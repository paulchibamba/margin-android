package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.llm.LlmCall
import com.paulchibamba.margin.domain.llm.LlmClient
import com.paulchibamba.margin.domain.llm.LlmExchange
import com.paulchibamba.margin.domain.llm.LlmModel
import com.paulchibamba.margin.domain.llm.LlmPriceTable
import com.paulchibamba.margin.domain.llm.LlmReply
import com.paulchibamba.margin.domain.llm.LlmRequest
import com.paulchibamba.margin.domain.llm.MicroDollars
import com.paulchibamba.margin.domain.repository.ApiKeyStore
import com.paulchibamba.margin.domain.repository.Clock
import com.paulchibamba.margin.domain.repository.LlmLedger
import com.paulchibamba.margin.domain.repository.LlmSettingsRepository
import com.paulchibamba.margin.domain.time.startOfToday
import javax.inject.Inject

class CallLlm @Inject constructor(
    private val keys: ApiKeyStore,
    private val settings: LlmSettingsRepository,
    private val ledger: LlmLedger,
    private val client: LlmClient,
    private val clock: Clock,
) {

    suspend operator fun invoke(request: LlmRequest): LlmReply {
        val key = keys.load() ?: return LlmReply.TemplatesOnly
        val current = settings.settings()
        if (isOverCap(current.dailyCap)) return LlmReply.OverCap
        val exchange = client.send(key, current.bakeModel, request)
        ledger.record(callOf(request, current.bakeModel, exchange))
        return replyOf(exchange, current.bakeModel)
    }

    private suspend fun isOverCap(cap: MicroDollars): Boolean = ledger.spendSince(clock.startOfToday()).cost >= cap

    private fun callOf(request: LlmRequest, model: LlmModel, exchange: LlmExchange) = LlmCall(
        at = clock.now(),
        purpose = request.purpose,
        model = model,
        usage = exchange.usage,
        cost = LlmPriceTable.costOf(model, exchange.usage),
        isOk = exchange is LlmExchange.Answered,
        error = (exchange as? LlmExchange.Failed)?.failure?.detail,
    )

    private fun replyOf(exchange: LlmExchange, model: LlmModel): LlmReply = when (exchange) {
        is LlmExchange.Answered -> LlmReply.Answered(exchange.json, model)
        is LlmExchange.Failed -> LlmReply.Failed(exchange.failure.reason)
    }
}
