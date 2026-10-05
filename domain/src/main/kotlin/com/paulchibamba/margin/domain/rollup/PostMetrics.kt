package com.paulchibamba.margin.domain.rollup

import com.paulchibamba.margin.domain.model.Format

data class PostMetrics(
    val postsSeen: Int,
    val exposures: Int,
    val glances: Int,
    val deepReads: Int,
    val ratioByFormat: Map<Format, FormatAttention>,
) {
    val glanceRate: Double?
        get() = Share.of(glances, exposures)

    val deepRate: Double?
        get() = Share.of(deepReads, exposures)
}
