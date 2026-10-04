package com.paulchibamba.margin.data.database.dao

import androidx.room.Dao
import androidx.room.Query

@Dao
interface SessionTallyDao {

    @Query("SELECT COUNT(*) FROM post_seen WHERE lastSeenAt BETWEEN :from AND :to")
    suspend fun postsSeenBetween(from: Long, to: Long): Int

    @Query("SELECT COUNT(*) FROM note_read WHERE readAt BETWEEN :from AND :to")
    suspend fun notesReadBetween(from: Long, to: Long): Int
}
