package com.paulchibamba.margin.domain.bake

import com.paulchibamba.margin.domain.drop.DropHeadline
import java.time.Instant

data class BakeState(
    val lastBakeAt: Instant? = null,
    val seenSeeds: Set<String> = emptySet(),
    val nextDropHeadline: DropHeadline? = null,
)
