package com.paulchibamba.margin.domain.signals

import com.paulchibamba.margin.domain.model.PostContent
import kotlin.math.min
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class EngagementCalculator(private val fastSkip: Duration = 1200.milliseconds) {

    fun score(content: PostContent, exit: PostExit): Double {
        if (isFastSkip(exit)) return 0.0
        val readFraction = readFraction(content, exit.dwell)
        return if (content.format.isInteractive) interactiveScore(readFraction, exit.isEngaged) else readFraction
    }

    private fun isFastSkip(exit: PostExit): Boolean = !exit.isEngaged && exit.dwell < fastSkip

    private fun readFraction(content: PostContent, dwell: Duration): Double =
        min(1.0, dwell / ExpectedReadTime.of(content))

    private fun interactiveScore(readFraction: Double, isEngaged: Boolean): Double =
        READING_WEIGHT * readFraction + INTERACTION_WEIGHT * (if (isEngaged) 1.0 else 0.0)

    private companion object {
        const val READING_WEIGHT = 0.6
        const val INTERACTION_WEIGHT = 0.4
    }
}
