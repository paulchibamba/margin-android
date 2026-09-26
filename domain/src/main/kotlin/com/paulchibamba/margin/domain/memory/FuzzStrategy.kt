package com.paulchibamba.margin.domain.memory

import java.time.Instant

fun interface FuzzStrategy {
    fun factorFor(card: MemoryCard, reviewTime: Instant): Double?
}
