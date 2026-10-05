package com.paulchibamba.margin.feature.stats

import com.paulchibamba.margin.domain.memory.Rating
import com.paulchibamba.margin.domain.model.NotePosition
import com.paulchibamba.margin.domain.rollup.TimeOfDay
import kotlin.math.roundToInt

fun Rating.label(): String = when (this) {
    Rating.AGAIN -> "Again"
    Rating.HARD -> "Hard"
    Rating.GOOD -> "Good"
    Rating.EASY -> "Easy"
}

fun frontierLabel(frontier: NotePosition?): String =
    if (frontier == null) "Not started" else "Ch ${frontier.chapter} · note ${frontier.order + 1}"

fun percentLabel(share: Double): String = "${(share * 100).roundToInt()}%"

fun countLabel(count: Int): String = "%,d".format(count)

fun paceLabel(wordsPerMinute: Int): String = "${countLabel(wordsPerMinute)} wpm"

fun TimeOfDay.label(): String = when (this) {
    TimeOfDay.MORNING -> "Morning"
    TimeOfDay.AFTERNOON -> "Afternoon"
    TimeOfDay.EVENING -> "Evening"
    TimeOfDay.NIGHT -> "Night"
}
