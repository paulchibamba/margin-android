package com.paulchibamba.margin.feature.read.home

import com.paulchibamba.margin.domain.model.BookSlug

data class BookRingState(
    val book: BookSlug,
    val label: String,
    val progress: Float,
    val isActive: Boolean,
    val coverPath: String? = null,
)
