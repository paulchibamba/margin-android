package com.paulchibamba.margin.feature.feed

import com.paulchibamba.margin.domain.feed.CandidateSource
import com.paulchibamba.margin.domain.feed.ranking.ScorePart
import com.paulchibamba.margin.domain.memory.CardState
import com.paulchibamba.margin.domain.model.Format
import kotlin.math.ceil
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

private val CHAPTER_PREFIX =
    Regex("""^\s*(chapter\s+\d+\s*[:.\-–—]?|\d+[:.\-–—]?\s)\s*""", RegexOption.IGNORE_CASE)

fun Format.label(): String = when (this) {
    Format.CAROUSEL -> "Carousel"
    Format.FACT -> "Fact"
    Format.TIP -> "Tip"
    Format.ANALOGY -> "Analogy"
    Format.DIALOGUE -> "Dialogue"
    Format.VERSUS -> "Versus"
    Format.MYTH -> "Myth"
    Format.CHECKLIST -> "Checklist"
    Format.CODE_EXAMPLE -> "Code example"
    Format.MEME -> "Meme"
    Format.SOURCE -> "From the book"
    Format.MCQ -> "Quiz"
    Format.TRUE_FALSE -> "True or false"
    Format.RECALL -> "Recall"
    Format.FILL_BLANK -> "Fill the gap"
    Format.SPOT_BUG -> "Spot the bug"
    Format.SCENARIO -> "Scenario"
}

fun CandidateSource.label(): String = when (this) {
    CandidateSource.NEW -> "New"
    CandidateSource.REVIEW -> "Review"
    CandidateSource.ANGLE -> "Another angle"
    CandidateSource.RESURFACE -> "Resurfaced"
    CandidateSource.DELIGHT -> "Delight"
    CandidateSource.PREVIEW -> "Preview"
}

fun ScorePart.label(): String = when (this) {
    ScorePart.SOURCE -> "Source"
    ScorePart.FORMAT -> "Format"
    ScorePart.NOVELTY -> "Novelty"
    ScorePart.JITTER -> "Jitter"
    ScorePart.URGENCY -> "Urgency"
    ScorePart.PRIORITY -> "Priority"
    ScorePart.BOOK_WAIT -> "Book wait"
    ScorePart.RETEACH -> "Reteach"
    ScorePart.PROVE_IT -> "Prove it"
}

fun CardState.label(): String = when (this) {
    CardState.NEW -> "New"
    CardState.LEARNING -> "Learning"
    CardState.REVIEW -> "Review"
    CardState.RELEARNING -> "Relearning"
}

fun chapterLabel(number: Int, title: String): String {
    val shortTitle = shortChapterTitle(title)
    return if (shortTitle.isEmpty()) "Ch $number" else "Ch $number · $shortTitle"
}

fun shortChapterTitle(title: String): String = title.replace(CHAPTER_PREFIX, "").trim()

fun readingTimeLabel(readingTime: Duration): String = "~${wholeMinutesOf(readingTime)} min"

fun readToUnlockLabel(readingTime: Duration): String = "Read to unlock · ${wholeMinutesOf(readingTime)} min"

fun notesAheadLabel(noteCount: Int): String = if (noteCount == 1) "1 note ahead" else "$noteCount notes ahead"

private fun wholeMinutesOf(duration: Duration): Int = ceil(duration.inWholeSeconds / 60.0).toInt().coerceAtLeast(1)

fun dueInLabel(dueIn: Duration): String = when {
    dueIn < 1.minutes -> "in under a minute"
    dueIn < 1.hours -> "in ${dueIn.inWholeMinutes} min"
    dueIn < 1.days -> "in ${dueIn.inWholeHours} h"
    else -> "in ${dueIn.inWholeDays} d"
}

fun nextNoteDetail(readingTime: Duration, unlockedPosts: Int): String {
    val time = readingTimeLabel(readingTime)
    return when (unlockedPosts) {
        0 -> time
        1 -> "$time · unlocks 1 post"
        else -> "$time · unlocks $unlockedPosts posts"
    }
}

fun approximateIntervalLabel(interval: Duration): String = when {
    interval < 1.hours -> "~${roundedMinutesOf(interval)} min"
    interval < 1.days -> "~${roundedHoursOf(interval)} h"
    else -> roundedDaysOf(interval).let { days -> if (days == 1) "~1 day" else "~$days days" }
}

fun shortIntervalLabel(interval: Duration): String = when {
    interval < 1.hours -> "${roundedMinutesOf(interval)}m"
    interval < 1.days -> "${roundedHoursOf(interval)}h"
    else -> "${roundedDaysOf(interval)}d"
}

private fun roundedMinutesOf(interval: Duration): Int = (interval / 1.minutes).roundToInt().coerceAtLeast(1)

private fun roundedHoursOf(interval: Duration): Int = (interval / 1.hours).roundToInt()

private fun roundedDaysOf(interval: Duration): Int = (interval / 1.days).roundToInt()
