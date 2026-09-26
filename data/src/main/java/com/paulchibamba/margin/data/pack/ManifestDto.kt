package com.paulchibamba.margin.data.pack

import kotlinx.serialization.Serializable

@Serializable
data class ManifestDto(
    val version: String,
    val books: List<ManifestBookDto>,
    val library: String = "library.json",
)
