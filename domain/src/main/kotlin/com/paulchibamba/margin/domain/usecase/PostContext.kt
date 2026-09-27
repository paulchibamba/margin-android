package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.rewards.BookCompletion

data class PostContext(
    val conceptTitle: String,
    val bookTitle: String,
    val chapterNumber: Int,
    val chapterTitle: String,
    val completion: BookCompletion,
    val sourceNote: NoteId?,
    val readingAhead: ReadingAhead? = null,
)
