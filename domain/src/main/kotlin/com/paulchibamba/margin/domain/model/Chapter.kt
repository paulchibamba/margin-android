package com.paulchibamba.margin.domain.model

data class Chapter(
    val bookSlug: BookSlug,
    val number: Int,
    val title: String,
    val isReadingOnly: Boolean,
)
