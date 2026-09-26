package com.paulchibamba.margin.domain.model

import kotlin.time.Duration

data class Note(
    val id: NoteId,
    val bookSlug: BookSlug,
    val position: NotePosition,
    val section: String,
    val part: Int,
    val partCount: Int,
    val html: String,
    val wordCount: Int,
    val readingTime: Duration,
)
