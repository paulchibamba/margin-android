package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.model.BookCover
import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.repository.BookCoverRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeBookCoverRepository : BookCoverRepository {
    val covers = MutableStateFlow(emptyMap<BookSlug, BookCover>())

    override fun observeCovers(): Flow<List<BookCover>> = covers.map { it.values.toList() }
    override suspend fun cover(book: BookSlug): BookCover? = covers.value[book]
    override suspend fun saveCover(cover: BookCover) {
        covers.value += cover.book to cover
    }

    override suspend fun removeCover(book: BookSlug) {
        covers.value -= book
    }
}
