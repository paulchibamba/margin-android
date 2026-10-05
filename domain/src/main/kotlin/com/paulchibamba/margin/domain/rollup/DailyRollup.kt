package com.paulchibamba.margin.domain.rollup

import java.time.Instant
import java.time.LocalDate

data class DailyRollup(val date: LocalDate, val metrics: RollupMetrics, val computedAt: Instant)
