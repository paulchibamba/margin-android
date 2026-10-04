package com.paulchibamba.margin.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.paulchibamba.margin.data.database.entity.BookSettingsEntity
import com.paulchibamba.margin.data.database.entity.ReadingOnlyChapterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {

    @Upsert
    suspend fun upsertBookSettings(settings: BookSettingsEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBookSettingsIfAbsent(settings: List<BookSettingsEntity>): List<Long>

    @Query("SELECT * FROM book_settings")
    fun bookSettings(): Flow<List<BookSettingsEntity>>

    @Query("SELECT * FROM book_settings WHERE bookSlug = :bookSlug")
    suspend fun bookSettingsOf(bookSlug: String): BookSettingsEntity?

    @Query("SELECT * FROM reading_only_chapter")
    fun readingOnlyChapters(): Flow<List<ReadingOnlyChapterEntity>>

    @Query("SELECT chapter FROM reading_only_chapter WHERE bookSlug = :bookSlug ORDER BY chapter")
    suspend fun readingOnlyChaptersOf(bookSlug: String): List<Int>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertReadingOnlyChapters(chapters: List<ReadingOnlyChapterEntity>)

    @Transaction
    suspend fun replaceReadingOnlyChapters(bookSlug: String, chapters: List<Int>) {
        deleteReadingOnlyChapters(bookSlug)
        insertReadingOnlyChapters(chapters.map { chapter -> ReadingOnlyChapterEntity(bookSlug, chapter) })
    }

    @Query("DELETE FROM reading_only_chapter WHERE bookSlug = :bookSlug")
    suspend fun deleteReadingOnlyChapters(bookSlug: String)
}
