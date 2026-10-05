package com.paulchibamba.margin.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.paulchibamba.margin.data.database.entity.GeneratedPostEntity

@Dao
interface GeneratedPostDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(posts: List<GeneratedPostEntity>): List<Long>

    @Query("SELECT * FROM generated_post WHERE shownAt IS NULL AND lessPressed = 0 ORDER BY createdAt")
    suspend fun unseen(): List<GeneratedPostEntity>

    @Query("SELECT * FROM generated_post ORDER BY createdAt")
    suspend fun all(): List<GeneratedPostEntity>

    @Query("UPDATE generated_post SET shownAt = :at WHERE id = :id AND shownAt IS NULL")
    suspend fun markShown(id: String, at: Long)

    @Query("UPDATE generated_post SET lessPressed = 1 WHERE id = :id")
    suspend fun markLess(id: String)

    @Query("SELECT noteId FROM note_read")
    suspend fun readNoteIds(): List<String>

    @Query("SELECT id FROM concept")
    suspend fun conceptIds(): List<String>
}
