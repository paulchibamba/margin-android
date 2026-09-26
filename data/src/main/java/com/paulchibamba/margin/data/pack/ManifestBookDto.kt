package com.paulchibamba.margin.data.pack

import kotlinx.serialization.Serializable

@Serializable
data class ManifestBookDto(
    val slug: String,
    val title: String,
    val file: String,
)
