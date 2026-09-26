package com.paulchibamba.margin.domain.model

import kotlin.time.Duration

data class NoteOutline(
    val id: NoteId,
    val bookSlug: BookSlug,
    val position: NotePosition,
    val section: String,
    val readingTime: Duration,
)
