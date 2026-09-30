package com.paulchibamba.margin.domain.model

import java.time.Instant

data class BookCover(val book: BookSlug, val imagePath: String, val updatedAt: Instant)
