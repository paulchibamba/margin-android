package com.paulchibamba.margin.domain.llm

interface LlmClient {
    suspend fun send(key: ApiKey, model: LlmModel, request: LlmRequest): LlmExchange
}
