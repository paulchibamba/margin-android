package com.paulchibamba.margin.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.paulchibamba.margin.data.database.entity.FeedHistoryEntity
import com.paulchibamba.margin.data.database.entity.FormatAffinityEntity
import com.paulchibamba.margin.data.database.entity.PostSeenEntity
import com.paulchibamba.margin.data.database.entity.SavedPostEntity

@Dao
interface FeedStateDao {

    @Upsert
    suspend fun upsertSeen(seen: List<PostSeenEntity>)

    @Query("SELECT * FROM post_seen")
    suspend fun seenPosts(): List<PostSeenEntity>

    @Upsert
    suspend fun upsertAffinity(affinity: List<FormatAffinityEntity>)

    @Query("SELECT * FROM format_affinity")
    suspend fun affinity(): List<FormatAffinityEntity>

    @Upsert
    suspend fun upsertHistory(history: List<FeedHistoryEntity>)

    @Query("DELETE FROM feed_history WHERE step <= (SELECT MAX(step) FROM feed_history) - :keep")
    suspend fun trimHistory(keep: Int)

    @Query("SELECT * FROM feed_history ORDER BY step")
    suspend fun history(): List<FeedHistoryEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSaved(saved: SavedPostEntity)

    @Query("SELECT * FROM saved_post ORDER BY savedAt")
    suspend fun savedPosts(): List<SavedPostEntity>
}
