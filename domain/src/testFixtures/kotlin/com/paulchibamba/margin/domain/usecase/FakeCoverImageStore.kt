package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.model.CoverSource
import com.paulchibamba.margin.domain.repository.CoverImageStore
import java.time.Instant

class FakeCoverImageStore : CoverImageStore {
    val files = mutableSetOf<String>()
    var canRead = true

    override suspend fun save(book: BookSlug, source: CoverSource, at: Instant): String? {
        if (!canRead) return null
        return "covers/${book.value}-${at.toEpochMilli()}.webp".also(files::add)
    }

    override suspend fun delete(imagePath: String) {
        files -= imagePath
    }
}
