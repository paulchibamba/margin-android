package com.paulchibamba.margin.data.pack

import kotlinx.serialization.Serializable

@Serializable
data class BookFileDto(
    val slug: String,
    val title: String,
    val chapters: List<ChapterDto> = emptyList(),
    val concepts: List<ConceptDto> = emptyList(),
    val notes: List<NoteDto> = emptyList(),
)
