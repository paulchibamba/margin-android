package com.paulchibamba.margin.domain.progress

import java.time.Instant

internal data class RecallOutcome(val at: Instant, val isPass: Boolean)
