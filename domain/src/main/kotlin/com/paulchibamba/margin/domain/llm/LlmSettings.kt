package com.paulchibamba.margin.domain.llm

data class LlmSettings(
    val bakeModel: LlmModel = LlmModel.DEFAULT_BAKE,
    val dailyCap: MicroDollars = DEFAULT_DAILY_CAP,
    val isSendingExcerpts: Boolean = true,
) {
    companion object {
        val DEFAULT_DAILY_CAP = MicroDollars(20_000)
        val DAILY_CAP_CHOICES = listOf(
            MicroDollars(10_000),
            DEFAULT_DAILY_CAP,
            MicroDollars(50_000),
            MicroDollars(100_000),
        )
    }
}
