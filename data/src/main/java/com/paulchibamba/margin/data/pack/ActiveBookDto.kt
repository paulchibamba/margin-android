package com.paulchibamba.margin.data.pack

import kotlinx.serialization.Serializable

@Serializable
data class ActiveBookDto(
    val slug: String,
    val priority: String,
)
