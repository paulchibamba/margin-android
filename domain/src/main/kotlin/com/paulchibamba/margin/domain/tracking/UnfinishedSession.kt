package com.paulchibamba.margin.domain.tracking

import java.time.Instant

data class UnfinishedSession(val id: SessionId, val startedAt: Instant, val lastEventAt: Instant)
