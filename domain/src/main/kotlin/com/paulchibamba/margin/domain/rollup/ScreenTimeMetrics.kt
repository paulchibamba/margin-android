package com.paulchibamba.margin.domain.rollup

import kotlin.time.Duration

data class ScreenTimeMetrics(
    val screen: Duration,
    val doom: Duration,
    val margin: Duration,
    val topDoomApps: List<String>,
) {
    val marginShare: Double?
        get() = (margin + doom).takeIf(Duration::isPositive)?.let { total -> margin / total }
}
