package com.paulchibamba.margin.feature.settings

import com.paulchibamba.margin.domain.model.DarkMode
import java.util.Locale

fun activeBooksLabel(activeCount: Int, maxActive: Int): String = "$activeCount of $maxActive max"

fun retentionLabel(retention: Double): String = String.format(Locale.US, "%.2f", retention)

fun workloadWarning(steepAbove: Double): String = "Above ${retentionLabel(steepAbove)}, reviews rise steeply."

fun darkModeLabel(mode: DarkMode): String = when (mode) {
    DarkMode.OFF -> "Off"
    DarkMode.FOLLOW_SYSTEM -> "System"
    DarkMode.ALWAYS -> "Always"
}
