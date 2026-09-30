package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.model.BookCover
import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.model.CoverSource
import com.paulchibamba.margin.domain.repository.BookCoverRepository
import com.paulchibamba.margin.domain.repository.Clock
import com.paulchibamba.margin.domain.repository.CoverImageStore
import javax.inject.Inject

class SetBookCover @Inject constructor(
    private val covers: BookCoverRepository,
    private val images: CoverImageStore,
    private val clock: Clock,
) {

    suspend operator fun invoke(book: BookSlug, source: CoverSource): Boolean {
        val now = clock.now()
        val imagePath = images.save(book, source, now) ?: return false
        val previous = covers.cover(book)
        covers.saveCover(BookCover(book, imagePath, now))
        previous?.let { images.delete(it.imagePath) }
        return true
    }
}
