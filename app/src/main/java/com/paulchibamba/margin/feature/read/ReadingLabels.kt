package com.paulchibamba.margin.feature.read

import com.paulchibamba.margin.domain.model.Priority
import com.paulchibamba.margin.domain.usecase.NoteTally
import kotlin.math.ceil
import kotlin.time.Duration

private const val MINUTES_PER_HOUR = 60
private val LEADING_ARTICLE = Regex("""^the\s+""", RegexOption.IGNORE_CASE)

fun timeLeftLabel(timeLeft: Duration): String {
    val minutes = ceil(timeLeft.inWholeSeconds / 60.0).toInt()
    val hours = minutes / MINUTES_PER_HOUR
    val remainder = minutes % MINUTES_PER_HOUR
    return when {
        hours == 0 -> "$minutes min"
        remainder == 0 -> "$hours h"
        else -> "$hours h $remainder m"
    }
}

fun bookProgressLabel(tally: NoteTally): String {
    val notes = "${tally.notesRead}/${tally.noteCount} notes"
    return if (tally.isFinished) "$notes · all read" else "$notes · ${timeLeftLabel(tally.timeLeft)} left"
}

fun ringLabels(titles: List<String>): List<String> {
    val words = titles.map { title -> title.replace(LEADING_ARTICLE, "").split(" ").filter(String::isNotBlank) }
    return words.mapIndexed { index, title ->
        val shared = words.filterIndexed { other, _ -> other != index }.maxOfOrNull { sharedPrefixLength(title, it) }
        title.drop(shared?.coerceAtMost(title.size - 1) ?: 0).joinToString(" ")
    }
}

private fun sharedPrefixLength(first: List<String>, second: List<String>): Int =
    first.zip(second).takeWhile { (one, other) -> one == other }.size

fun Priority.label(): String = when (this) {
    Priority.MAIN -> "Main"
    Priority.NORMAL -> "Normal"
    Priority.LOW -> "Low"
}
