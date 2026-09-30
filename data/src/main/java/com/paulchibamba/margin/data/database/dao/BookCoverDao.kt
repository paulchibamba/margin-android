package com.paulchibamba.margin.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.paulchibamba.margin.data.database.entity.BookCoverEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookCoverDao {

    @Query("SELECT * FROM book_cover")
    fun covers(): Flow<List<BookCoverEntity>>

    @Query("SELECT * FROM book_cover WHERE bookSlug = :bookSlug")
    suspend fun cover(bookSlug: String): BookCoverEntity?

    @Upsert
    suspend fun upsert(cover: BookCoverEntity)

    @Query("DELETE FROM book_cover WHERE bookSlug = :bookSlug")
    suspend fun delete(bookSlug: String)
}
