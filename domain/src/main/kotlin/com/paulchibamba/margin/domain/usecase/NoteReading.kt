package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.model.Note
import com.paulchibamba.margin.domain.model.NoteId

data class NoteReading(
    val note: Note,
    val chapterTitle: String,
    val place: PlaceInChapter,
    val isRead: Boolean,
    val previous: NoteId?,
    val next: NoteId?,
)
