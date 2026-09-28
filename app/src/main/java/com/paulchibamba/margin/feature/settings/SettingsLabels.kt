package com.paulchibamba.margin.feature.settings

import java.util.Locale

fun activeBooksLabel(activeCount: Int, maxActive: Int): String = "$activeCount of $maxActive max"

fun retentionLabel(retention: Double): String = String.format(Locale.US, "%.2f", retention)

fun workloadWarning(steepAbove: Double): String = "Above ${retentionLabel(steepAbove)}, reviews rise steeply."
