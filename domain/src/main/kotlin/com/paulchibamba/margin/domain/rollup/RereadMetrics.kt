package com.paulchibamba.margin.domain.rollup

data class RereadMetrics(
    val revisits: Int,
    val notesReopened: Int,
    val scrollBacks: Int,
    val hotspots: List<RereadHotspot>,
)
