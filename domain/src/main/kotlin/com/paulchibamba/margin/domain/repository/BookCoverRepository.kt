package com.paulchibamba.margin.domain.repository

import com.paulchibamba.margin.domain.model.BookCover
import com.paulchibamba.margin.domain.model.BookSlug
import kotlinx.coroutines.flow.Flow

interface BookCoverRepository {
    fun observeCovers(): Flow<List<BookCover>>
    suspend fun cover(book: BookSlug): BookCover?
    suspend fun saveCover(cover: BookCover)
    suspend fun removeCover(book: BookSlug)
}
