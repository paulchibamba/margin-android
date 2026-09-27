package com.paulchibamba.margin.feature.read.book

import com.paulchibamba.margin.domain.model.ChapterRef
import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.usecase.NoteTally

data class ChapterRowState(
    val chapter: ChapterRef,
    val title: String,
    val tally: NoteTally,
    val isReadingOnly: Boolean,
    val isKnown: Boolean,
    val isDone: Boolean,
    val isCurrent: Boolean,
    val isAhead: Boolean,
    val nextNote: NoteId?,
)
