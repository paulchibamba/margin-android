package com.paulchibamba.margin.data.pack

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LibraryDto(
    val active: List<ActiveBookDto> = emptyList(),
    @SerialName("reading_only") val readingOnly: Map<String, List<Int>> = emptyMap(),
    @SerialName("priority_share") val priorityShare: Map<String, Double> = emptyMap(),
    @SerialName("preview_notes_ahead") val previewNotesAhead: Int? = null,
)
