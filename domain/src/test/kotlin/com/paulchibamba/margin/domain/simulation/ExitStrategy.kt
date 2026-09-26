package com.paulchibamba.margin.domain.simulation

import com.paulchibamba.margin.domain.feed.FeedItem
import com.paulchibamba.margin.domain.model.PostRole
import com.paulchibamba.margin.domain.signals.AnswerOutcome
import com.paulchibamba.margin.domain.signals.PostExit
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

fun interface ExitStrategy {

    fun exitFor(item: FeedItem, random: Random): PostExit

    companion object {
        private const val MOSTLY_RIGHT_ACCURACY = 0.8

        val MostlyRight = ExitStrategy { item, random ->
            PostExit(dwell = 5.seconds, isEngaged = true, answer = answerFor(item, random, MOSTLY_RIGHT_ACCURACY))
        }

        fun answerFor(item: FeedItem, random: Random, chanceOfRight: Double): AnswerOutcome? = when {
            item.post.role != PostRole.TEST -> null
            random.nextDouble() < chanceOfRight -> AnswerOutcome.Correct
            else -> AnswerOutcome.Wrong
        }
    }
}
