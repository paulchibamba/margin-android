package com.paulchibamba.margin.domain.simulation

import com.paulchibamba.margin.domain.feed.FeedItem
import com.paulchibamba.margin.domain.memory.CardState
import com.paulchibamba.margin.domain.model.Format
import com.paulchibamba.margin.domain.signals.PostExit
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

data class Persona(
    val name: String,
    val loves: Set<Format>,
    val hates: Set<Format>,
    val firstTestAccuracy: Double,
) : ExitStrategy {

    override fun exitFor(item: FeedItem, random: Random): PostExit {
        val format = item.post.format
        val dwell = when (format) {
            in hates -> 500.milliseconds
            in loves -> 15.seconds
            else -> 5.seconds
        }
        return PostExit(dwell, isEngaged = format !in hates, ExitStrategy.answerFor(item, random, chanceOfRight(item)))
    }

    private fun chanceOfRight(item: FeedItem): Double {
        val memory = item.memory
        return if (memory == null || memory.state == CardState.NEW) firstTestAccuracy else memory.recall
    }

    companion object {
        val Memelord = Persona(
            name = "memelord",
            loves = setOf(Format.MEME, Format.SPOT_BUG),
            hates = setOf(Format.CHECKLIST),
            firstTestAccuracy = 0.7,
        )
        val Quizzer = Persona(
            name = "quizzer",
            loves = setOf(Format.MCQ, Format.SCENARIO, Format.TRUE_FALSE),
            hates = setOf(Format.CAROUSEL, Format.DIALOGUE),
            firstTestAccuracy = 0.9,
        )
        val Skimmer = Persona(
            name = "skimmer",
            loves = emptySet(),
            hates = setOf(Format.CAROUSEL, Format.CHECKLIST, Format.CODE_EXAMPLE, Format.DIALOGUE),
            firstTestAccuracy = 0.5,
        )
        val All = listOf(Memelord, Quizzer, Skimmer)
    }
}
