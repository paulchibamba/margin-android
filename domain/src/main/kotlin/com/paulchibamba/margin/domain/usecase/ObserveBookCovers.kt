package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.model.BookCover
import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.repository.BookCoverRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveBookCovers @Inject constructor(private val covers: BookCoverRepository) {

    operator fun invoke(): Flow<Map<BookSlug, BookCover>> =
        covers.observeCovers().map { covers -> covers.associateBy(BookCover::book) }
}
