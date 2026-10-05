package com.paulchibamba.margin.feature.settings.screentime

import com.paulchibamba.margin.domain.screentime.AppCategory
import kotlin.time.Duration

private const val MINUTES_PER_HOUR = 60

fun minutesLabel(duration: Duration): String {
    val minutes = duration.inWholeMinutes.toInt()
    val hours = minutes / MINUTES_PER_HOUR
    val rest = minutes % MINUTES_PER_HOUR
    return when {
        hours == 0 -> "$minutes min"
        rest == 0 -> "$hours h"
        else -> "$hours h $rest min"
    }
}

fun AppCategory.label(): String = when (this) {
    AppCategory.SOCIAL -> "Social"
    AppCategory.VIDEO -> "Video"
    AppCategory.GAME -> "Game"
    AppCategory.AUDIO -> "Audio"
    AppCategory.IMAGE -> "Photos"
    AppCategory.NEWS -> "News"
    AppCategory.MAPS -> "Maps"
    AppCategory.PRODUCTIVITY -> "Productivity"
    AppCategory.ACCESSIBILITY -> "Accessibility"
    AppCategory.OTHER -> "Other"
    AppCategory.MARGIN -> "Margin"
}
