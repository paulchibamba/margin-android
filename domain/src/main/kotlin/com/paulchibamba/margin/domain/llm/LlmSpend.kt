package com.paulchibamba.margin.domain.llm

data class LlmSpend(val calls: Int, val cost: MicroDollars) {

    companion object {
        val NONE = LlmSpend(calls = 0, cost = MicroDollars.ZERO)
    }
}
