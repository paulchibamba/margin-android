package com.paulchibamba.margin.domain.feed

import com.paulchibamba.margin.domain.memory.FsrsScheduler
import com.paulchibamba.margin.domain.memory.MemoryCard
import com.paulchibamba.margin.domain.memory.Rating
import com.paulchibamba.margin.domain.model.Post
import java.time.Instant
import kotlin.time.Duration

class CardGrader(private val scheduler: FsrsScheduler) {

    fun grade(card: MemoryCard, post: Post, rating: Rating, now: Instant, dwell: Duration?): GradedCard {
        val next = scheduler.next(card, now, rating)
        val logEntry = ReviewLogEntry(
            at = now,
            conceptId = post.conceptId,
            postId = post.id,
            rating = rating,
            stateBefore = card.state,
            stabilityBefore = card.stability,
            stabilityAfter = next.stability,
            dwell = dwell,
        )
        return GradedCard(next, logEntry)
    }
}
