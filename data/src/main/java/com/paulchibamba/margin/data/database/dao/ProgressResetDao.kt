package com.paulchibamba.margin.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.paulchibamba.margin.data.database.MetaKey

@Dao
interface ProgressResetDao {

    @Transaction
    suspend fun clearProgress() {
        clearConceptProgress()
        clearReviewLog()
        clearPostSeen()
        clearActionLog()
        clearFormatAffinity()
        clearNotesRead()
        clearChaptersKnown()
        clearSavedPosts()
        clearFeedHistory()
        clearDailyActivity()
        clearGeneratedPosts()
        clearMeta(MetaKey.PROGRESS_KEYS)
        MetaKey.PROGRESS_PREFIXES.forEach { prefix -> clearMetaWithPrefix(prefix) }
    }

    @Query("DELETE FROM concept_progress")
    suspend fun clearConceptProgress()

    @Query("DELETE FROM review_log")
    suspend fun clearReviewLog()

    @Query("DELETE FROM post_seen")
    suspend fun clearPostSeen()

    @Query("DELETE FROM action_log")
    suspend fun clearActionLog()

    @Query("DELETE FROM format_affinity")
    suspend fun clearFormatAffinity()

    @Query("DELETE FROM note_read")
    suspend fun clearNotesRead()

    @Query("DELETE FROM chapter_known")
    suspend fun clearChaptersKnown()

    @Query("DELETE FROM saved_post")
    suspend fun clearSavedPosts()

    @Query("DELETE FROM feed_history")
    suspend fun clearFeedHistory()

    @Query("DELETE FROM daily_activity")
    suspend fun clearDailyActivity()

    @Query("DELETE FROM generated_post")
    suspend fun clearGeneratedPosts()

    @Query("DELETE FROM meta WHERE `key` IN (:keys)")
    suspend fun clearMeta(keys: List<String>)

    @Query("DELETE FROM meta WHERE `key` LIKE :prefix || '%'")
    suspend fun clearMetaWithPrefix(prefix: String)
}
