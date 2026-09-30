package com.paulchibamba.margin.data.repository

import com.paulchibamba.margin.data.cover.CoverDirectory
import com.paulchibamba.margin.data.database.MarginDatabase
import com.paulchibamba.margin.data.database.entity.BookCoverEntity
import com.paulchibamba.margin.domain.model.BookCover
import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.repository.BookCoverRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomBookCoverRepository @Inject constructor(
    private val database: MarginDatabase,
    private val directory: CoverDirectory,
) : BookCoverRepository {
    private val coverDao get() = database.bookCoverDao()

    override fun observeCovers(): Flow<List<BookCover>> = coverDao.covers().map { covers -> covers.map(::toDomain) }

    override suspend fun cover(book: BookSlug): BookCover? = coverDao.cover(book.value)?.let(::toDomain)

    override suspend fun saveCover(cover: BookCover) {
        coverDao.upsert(BookCoverEntity(cover.book.value, File(cover.imagePath).name, cover.updatedAt.toEpochMilli()))
    }

    override suspend fun removeCover(book: BookSlug) {
        coverDao.delete(book.value)
    }

    private fun toDomain(entity: BookCoverEntity) = BookCover(
        book = BookSlug(entity.bookSlug),
        imagePath = directory.fileOf(entity.fileName).path,
        updatedAt = Instant.ofEpochMilli(entity.updatedAt),
    )
}
