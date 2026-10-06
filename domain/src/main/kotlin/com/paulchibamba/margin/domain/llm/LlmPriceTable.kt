package com.paulchibamba.margin.domain.llm

object LlmPriceTable {

    private val prices = mapOf(
        LlmModel.GPT_5_NANO to priceOf(input = 0.05, cachedInput = 0.005, output = 0.40),
        LlmModel.GPT_6_LUNA to priceOf(input = 0.10, cachedInput = 0.01, output = 0.50),
        LlmModel.GPT_5_MINI to priceOf(input = 0.25, cachedInput = 0.025, output = 2.00),
        LlmModel.GPT_5_4_MINI to priceOf(input = 0.75, cachedInput = 0.075, output = 4.50),
    )

    private val mostExpensive = prices.values.maxBy { price -> price.outputPerMillion }

    fun priceOf(model: LlmModel): LlmPrice = prices[model] ?: mostExpensive

    fun costOf(model: LlmModel, usage: LlmUsage): MicroDollars = priceOf(model).costOf(usage)

    private fun priceOf(input: Double, cachedInput: Double, output: Double) = LlmPrice(
        inputPerMillion = MicroDollars.ofDollars(input),
        cachedInputPerMillion = MicroDollars.ofDollars(cachedInput),
        outputPerMillion = MicroDollars.ofDollars(output),
    )
}
