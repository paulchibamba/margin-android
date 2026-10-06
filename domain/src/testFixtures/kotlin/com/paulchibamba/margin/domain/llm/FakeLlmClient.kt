package com.paulchibamba.margin.domain.llm

class FakeLlmClient(
    var exchange: LlmExchange = LlmExchange.Answered("""{"ok":true}""", LlmUsage(100, 0, 20)),
) : LlmClient {
    val requests = mutableListOf<Pair<LlmModel, LlmRequest>>()

    override suspend fun send(key: ApiKey, model: LlmModel, request: LlmRequest): LlmExchange {
        requests += model to request
        return exchange
    }
}
