package com.paulchibamba.margin.domain.feed

import com.paulchibamba.margin.domain.memory.FsrsParameters
import com.paulchibamba.margin.domain.memory.FsrsScheduler
import com.paulchibamba.margin.domain.memory.MemoryCard
import com.paulchibamba.margin.domain.memory.NoFuzz
import com.paulchibamba.margin.domain.memory.Rating
import java.time.Instant

val testScheduler = FsrsScheduler(FsrsParameters.Default, NoFuzz)

fun learnedCard(reviewedAt: Instant = now.minusSeconds(86_400)): MemoryCard {
    val learning = testScheduler.next(MemoryCard.new(reviewedAt), reviewedAt, Rating.GOOD)
    return testScheduler.next(learning, reviewedAt.plusSeconds(600), Rating.GOOD)
}

fun learnedProgress(): ConceptProgress = introducedProgress().copy(card = learnedCard())
