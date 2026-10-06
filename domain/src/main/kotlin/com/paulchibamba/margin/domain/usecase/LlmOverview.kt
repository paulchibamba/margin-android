package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.llm.LlmSettings
import com.paulchibamba.margin.domain.llm.LlmSpend

data class LlmOverview(val maskedKey: String?, val settings: LlmSettings, val spentToday: LlmSpend) {

    val isTemplatesOnly: Boolean get() = maskedKey == null
}
