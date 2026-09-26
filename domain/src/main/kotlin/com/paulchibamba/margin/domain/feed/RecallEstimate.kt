package com.paulchibamba.margin.domain.feed

import com.paulchibamba.margin.domain.memory.CardState
import com.paulchibamba.margin.domain.memory.FsrsScheduler
import java.time.Instant

class RecallEstimate(private val scheduler: FsrsScheduler, private val config: FeedConfig) {

    fun of(progress: ConceptProgress, now: Instant): Double {
        val card = progress.card
        if (card == null || card.state == CardState.NEW) return config.desiredRetention - config.untestedRecallGap
        return scheduler.retrievability(card, now)
    }
}
