package com.paulchibamba.margin.domain.memory

import java.time.Instant
import java.util.Objects
import kotlin.random.Random

object CardSeededFuzz : FuzzStrategy {
    override fun factorFor(card: MemoryCard, reviewTime: Instant): Double {
        val seed = Objects.hash(reviewTime.toEpochMilli(), card.reps + 1, card.difficulty * card.stability)
        return Random(seed).nextDouble()
    }
}
