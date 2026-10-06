package com.paulchibamba.margin.domain.llm

@JvmInline
value class LlmModel(val id: String) {

    companion object {
        val GPT_5_NANO = LlmModel("gpt-5-nano")
        val GPT_6_LUNA = LlmModel("gpt-6-luna")
        val GPT_5_MINI = LlmModel("gpt-5-mini")
        val GPT_5_4_MINI = LlmModel("gpt-5.4-mini")
        val DEFAULT_BAKE = GPT_5_NANO
        val BAKE_CHOICES = listOf(GPT_5_NANO, GPT_6_LUNA, GPT_5_MINI)
    }
}
