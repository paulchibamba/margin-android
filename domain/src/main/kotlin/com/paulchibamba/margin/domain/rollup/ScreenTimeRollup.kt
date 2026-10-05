package com.paulchibamba.margin.domain.rollup

import com.paulchibamba.margin.domain.screentime.AppCategory
import com.paulchibamba.margin.domain.screentime.AppScreenTime
import kotlin.time.Duration

object ScreenTimeRollup {
    const val TOP_DOOM_APPS = 3

    fun of(apps: List<AppScreenTime>): ScreenTimeMetrics? {
        if (apps.isEmpty()) return null
        val doomApps = apps.filter(AppScreenTime::isDoom).sortedByDescending(AppScreenTime::foreground)
        return ScreenTimeMetrics(
            screen = apps.totalForeground(),
            doom = doomApps.totalForeground(),
            margin = apps.filter { app -> app.category == AppCategory.MARGIN }.totalForeground(),
            topDoomApps = doomApps.take(TOP_DOOM_APPS).map(AppScreenTime::label),
        )
    }

    private fun List<AppScreenTime>.totalForeground(): Duration =
        fold(Duration.ZERO) { total, app -> total + app.foreground }
}
