package com.paulchibamba.margin.domain.bake

import java.time.Instant

data class BakeState(
    val lastBakeAt: Instant? = null,
    val seenSeeds: Set<String> = emptySet(),
    val nextDropHeadline: String? = null,
)
