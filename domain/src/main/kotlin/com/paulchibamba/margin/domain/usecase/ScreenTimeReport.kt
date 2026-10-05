package com.paulchibamba.margin.domain.usecase

import kotlin.time.Duration

data class ScreenTimeReport(val days: List<ScreenTimeDay>) {
    val margin: Duration = days.fold(Duration.ZERO) { total, day -> total + day.margin }
    val doom: Duration = days.fold(Duration.ZERO) { total, day -> total + day.doom }
    val marginShare: Double? = (margin + doom).takeIf(Duration::isPositive)?.let { total -> margin / total }

    companion object {
        const val DAYS = 7L
    }
}
