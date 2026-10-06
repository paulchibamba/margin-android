package com.paulchibamba.margin.domain.llm

data class LlmFailure(val reason: LlmFailureReason, val detail: String)
