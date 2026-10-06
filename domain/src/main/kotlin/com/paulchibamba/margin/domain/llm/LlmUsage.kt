package com.paulchibamba.margin.domain.llm

data class LlmUsage(val tokensIn: Long, val tokensCached: Long, val tokensOut: Long) {

    val uncachedTokensIn: Long get() = (tokensIn - tokensCached).coerceAtLeast(0)

    companion object {
        val NONE = LlmUsage(tokensIn = 0, tokensCached = 0, tokensOut = 0)
    }
}
