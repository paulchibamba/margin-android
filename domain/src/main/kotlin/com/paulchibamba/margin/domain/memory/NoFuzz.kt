package com.paulchibamba.margin.domain.memory

import java.time.Instant

object NoFuzz : FuzzStrategy {
    override fun factorFor(card: MemoryCard, reviewTime: Instant): Double? = null
}
