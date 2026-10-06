package com.paulchibamba.margin.domain.llm

import kotlin.test.Test
import kotlin.test.assertEquals

class LlmPriceTableTest {

    @Test
    fun `a bake on gpt-5-nano costs about three hundred micro-dollars`() {
        val usage = LlmUsage(tokensIn = 2_000, tokensCached = 0, tokensOut = 600)

        assertEquals(MicroDollars(340), LlmPriceTable.costOf(LlmModel.GPT_5_NANO, usage))
    }

    @Test
    fun `cached input tokens are charged at the cached price`() {
        val usage = LlmUsage(tokensIn = 2_000, tokensCached = 1_000, tokensOut = 0)

        assertEquals(MicroDollars(55), LlmPriceTable.costOf(LlmModel.GPT_5_NANO, usage))
    }

    @Test
    fun `fractions of a micro-dollar are rounded up`() {
        val usage = LlmUsage(tokensIn = 1, tokensCached = 0, tokensOut = 0)

        assertEquals(MicroDollars(1), LlmPriceTable.costOf(LlmModel.GPT_5_NANO, usage))
    }

    @Test
    fun `a model missing from the table is priced like the most expensive one`() {
        val usage = LlmUsage(tokensIn = 1_000_000, tokensCached = 0, tokensOut = 1_000_000)

        assertEquals(
            LlmPriceTable.costOf(LlmModel.GPT_5_4_MINI, usage),
            LlmPriceTable.costOf(LlmModel("gpt-unknown"), usage),
        )
    }

    @Test
    fun `no usage costs nothing`() {
        assertEquals(MicroDollars.ZERO, LlmPriceTable.costOf(LlmModel.GPT_5_MINI, LlmUsage.NONE))
    }
}
