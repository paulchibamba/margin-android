package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.model.Chapter
import com.paulchibamba.margin.domain.model.NoteId

data class ChapterReading(
    val chapter: Chapter,
    val tally: NoteTally,
    val isKnown: Boolean,
    val nextNote: NoteId?,
) {
    val isDone: Boolean
        get() = isKnown || tally.isFinished
}
