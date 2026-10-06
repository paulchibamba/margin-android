package com.paulchibamba.margin.domain.llm

sealed interface LlmExchange {
    val usage: LlmUsage

    data class Answered(val json: String, override val usage: LlmUsage) : LlmExchange

    data class Failed(val failure: LlmFailure, override val usage: LlmUsage = LlmUsage.NONE) : LlmExchange
}
