package com.paulchibamba.margin.domain.llm

data class LlmPrice(
    val inputPerMillion: MicroDollars,
    val cachedInputPerMillion: MicroDollars,
    val outputPerMillion: MicroDollars,
) {

    fun costOf(usage: LlmUsage): MicroDollars {
        val scaled = usage.uncachedTokensIn * inputPerMillion.value +
            usage.tokensCached * cachedInputPerMillion.value +
            usage.tokensOut * outputPerMillion.value
        return MicroDollars(roundedUp(scaled))
    }

    private fun roundedUp(scaled: Long): Long = (scaled + TOKENS_PER_PRICE - 1) / TOKENS_PER_PRICE

    private companion object {
        const val TOKENS_PER_PRICE = 1_000_000L
    }
}
