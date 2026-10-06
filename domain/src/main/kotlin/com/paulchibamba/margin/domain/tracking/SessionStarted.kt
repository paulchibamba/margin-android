package com.paulchibamba.margin.domain.tracking

import java.time.Instant

data class SessionStarted(val at: Instant, val entry: SessionEntry)
