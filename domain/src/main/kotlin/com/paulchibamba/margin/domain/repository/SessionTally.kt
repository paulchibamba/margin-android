package com.paulchibamba.margin.domain.repository

import com.paulchibamba.margin.domain.tracking.SessionCounts
import java.time.Instant

fun interface SessionTally {
    suspend fun countBetween(from: Instant, to: Instant): SessionCounts
}
