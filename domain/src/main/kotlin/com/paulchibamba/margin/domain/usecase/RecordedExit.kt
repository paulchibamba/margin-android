package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.feed.ExitOutcome

data class RecordedExit(val outcome: ExitOutcome, val isStreakExtended: Boolean)
