package com.paulchibamba.margin.data.pack

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConceptDto(
    val id: String,
    val chapter: Int,
    val order: Int,
    val title: String,
    val summary: String = "",
    val section: String = "",
    @SerialName("note_id") val noteId: String? = null,
    val posts: List<PostDto> = emptyList(),
)
