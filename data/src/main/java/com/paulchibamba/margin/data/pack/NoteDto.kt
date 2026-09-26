package com.paulchibamba.margin.data.pack

import kotlinx.serialization.Serializable

@Serializable
data class NoteDto(
    val id: String,
    val chapter: Int,
    val order: Int,
    val section: String = "",
    val part: Int = 1,
    val parts: Int = 1,
    val html: String,
    val words: Int = 0,
    val minutes: Double = 0.0,
)
