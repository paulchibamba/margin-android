package com.paulchibamba.margin.domain.tracking

import java.time.Instant

data class LoggedEvent(val at: Instant, val sessionId: SessionId?, val event: Event)
