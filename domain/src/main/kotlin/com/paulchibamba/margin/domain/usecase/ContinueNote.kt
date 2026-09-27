package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.model.NoteOutline

data class ContinueNote(
    val outline: NoteOutline,
    val chapterTitle: String,
    val place: PlaceInChapter,
    val unlockedPosts: Int,
)
