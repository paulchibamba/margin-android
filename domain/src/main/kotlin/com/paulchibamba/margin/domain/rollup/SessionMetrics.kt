package com.paulchibamba.margin.domain.rollup

import com.paulchibamba.margin.domain.model.Format
import com.paulchibamba.margin.domain.tracking.SessionEntry
import kotlin.time.Duration

data class SessionMetrics(
    val sessions: Int,
    val medianSession: Duration?,
    val entries: Map<SessionEntry, Int>,
    val exitFormats: Map<Format, Int>,
)
