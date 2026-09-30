package com.paulchibamba.margin.domain.repository

import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.model.CoverSource
import java.time.Instant

interface CoverImageStore {
    suspend fun save(book: BookSlug, source: CoverSource, at: Instant): String?
    suspend fun delete(imagePath: String)
}
