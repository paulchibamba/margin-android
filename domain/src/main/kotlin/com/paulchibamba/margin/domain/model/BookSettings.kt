package com.paulchibamba.margin.domain.model

data class BookSettings(
    val bookSlug: BookSlug,
    val isActive: Boolean,
    val priority: Priority,
)
