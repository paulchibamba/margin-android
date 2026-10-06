package com.paulchibamba.margin.feature.settings.progressposts

import com.paulchibamba.margin.domain.llm.LlmModel
import com.paulchibamba.margin.domain.llm.LlmSpend
import com.paulchibamba.margin.domain.llm.MicroDollars
import kotlin.test.assertEquals
import org.junit.Test

class ProgressPostsLabelsTest {

    @Test
    fun `whole cents show two decimals and smaller amounts show every digit`() {
        assertEquals("$0.02", dollarsLabel(MicroDollars(20_000)))
        assertEquals("$0.10", dollarsLabel(MicroDollars(100_000)))
        assertEquals("$0.00034", dollarsLabel(MicroDollars(340)))
        assertEquals("$0.00", dollarsLabel(MicroDollars.ZERO))
    }

    @Test
    fun `today's spend counts calls`() {
        assertEquals("Spent today: $0.00 · 0 calls", spendLabel(LlmSpend.NONE))
        assertEquals("Spent today: $0.00034 · 1 call", spendLabel(LlmSpend(1, MicroDollars(340))))
    }

    @Test
    fun `model labels drop the gpt prefix`() {
        assertEquals("5 nano", modelLabel(LlmModel.GPT_5_NANO))
        assertEquals("6 luna", modelLabel(LlmModel.GPT_6_LUNA))
    }
}
