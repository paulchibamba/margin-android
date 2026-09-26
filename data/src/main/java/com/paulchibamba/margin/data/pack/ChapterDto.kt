package com.paulchibamba.margin.data.pack

import kotlinx.serialization.Serializable

@Serializable
data class ChapterDto(
    val index: Int,
    val title: String,
)
