package com.paulchibamba.margin.domain.screentime

import kotlin.time.Duration

data class AppScreenTime(
    val packageName: PackageName,
    val label: String,
    val category: AppCategory,
    val isDoom: Boolean,
    val foreground: Duration,
)
