package com.paulchibamba.margin.feature.stats

import com.paulchibamba.margin.domain.usecase.AttentionReport
import com.paulchibamba.margin.domain.usecase.StatsReport

data class StatsUiState(
    val report: StatsReport? = null,
    val attention: AttentionReport? = null,
    val exportText: String = "",
    val isCopied: Boolean = false,
) {
    val isLoading: Boolean
        get() = report == null
}
