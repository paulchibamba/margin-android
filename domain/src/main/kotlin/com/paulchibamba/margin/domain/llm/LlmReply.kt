package com.paulchibamba.margin.domain.llm

sealed interface LlmReply {

    data object TemplatesOnly : LlmReply

    data object OverCap : LlmReply

    data class Answered(val json: String, val model: LlmModel) : LlmReply

    data class Failed(val reason: LlmFailureReason) : LlmReply
}
