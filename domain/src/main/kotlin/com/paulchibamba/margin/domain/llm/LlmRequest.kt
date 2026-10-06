package com.paulchibamba.margin.domain.llm

data class LlmRequest(
    val purpose: LlmPurpose,
    val instructions: String,
    val input: String,
    val schema: LlmSchema,
    val maxOutputTokens: Int,
)
