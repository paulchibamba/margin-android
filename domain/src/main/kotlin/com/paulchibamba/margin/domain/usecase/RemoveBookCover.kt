package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.repository.BookCoverRepository
import com.paulchibamba.margin.domain.repository.CoverImageStore
import javax.inject.Inject

class RemoveBookCover @Inject constructor(
    private val covers: BookCoverRepository,
    private val images: CoverImageStore,
) {

    suspend operator fun invoke(book: BookSlug) {
        val cover = covers.cover(book) ?: return
        covers.removeCover(book)
        images.delete(cover.imagePath)
    }
}
