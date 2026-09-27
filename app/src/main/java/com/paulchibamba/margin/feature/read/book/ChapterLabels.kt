package com.paulchibamba.margin.feature.read.book

import com.paulchibamba.margin.feature.read.timeLeftLabel

fun chapterDetailLabel(row: ChapterRowState): String {
    val tally = row.tally
    val notes = "${tally.notesRead}/${tally.noteCount}"
    return when {
        row.isReadingOnly -> "Reading only · $notes"
        tally.isFinished -> "$notes notes"
        row.isKnown -> "Known · $notes"
        row.isCurrent -> "$notes · ${timeLeftLabel(tally.timeLeft)} left"
        else -> "$notes · ${timeLeftLabel(tally.timeLeft)}"
    }
}
