package com.paulchibamba.margin.domain.llm

import java.time.Instant

data class LlmCall(
    val at: Instant,
    val purpose: LlmPurpose,
    val model: LlmModel,
    val usage: LlmUsage,
    val cost: MicroDollars,
    val isOk: Boolean,
    val error: String?,
)
