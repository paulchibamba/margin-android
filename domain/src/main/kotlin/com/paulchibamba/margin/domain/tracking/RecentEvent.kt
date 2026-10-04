package com.paulchibamba.margin.domain.tracking

import java.time.Instant

data class RecentEvent(val at: Instant, val typeKey: String, val subjectId: String?, val props: String)
