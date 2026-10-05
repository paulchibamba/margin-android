package com.paulchibamba.margin.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.paulchibamba.margin.data.database.entity.EventEntity

@Dao
interface EventDao {

    @Insert
    suspend fun insertAll(events: List<EventEntity>)

    @Query("SELECT * FROM event_log WHERE type = 'session_start' ORDER BY id DESC LIMIT 1")
    suspend fun latestSessionStart(): EventEntity?

    @Query("SELECT COUNT(*) FROM event_log WHERE sessionId = :sessionId AND type = 'session_end'")
    suspend fun sessionEndCount(sessionId: String): Int

    @Query("SELECT MAX(at) FROM event_log WHERE sessionId = :sessionId")
    suspend fun lastEventAt(sessionId: String): Long?

    @Query("SELECT * FROM event_log ORDER BY id DESC LIMIT :limit")
    suspend fun latest(limit: Int): List<EventEntity>

    @Query("SELECT COUNT(*) FROM event_log WHERE type = :type AND subjectId = :subjectId")
    suspend fun countOf(type: String, subjectId: String): Int

    @Query("SELECT * FROM event_log WHERE at >= :from AND at < :until ORDER BY at, id")
    suspend fun between(from: Long, until: Long): List<EventEntity>

    @Query("SELECT MIN(at) FROM event_log")
    suspend fun firstAt(): Long?

    @Query("DELETE FROM event_log WHERE at < :cutoff")
    suspend fun deleteBefore(cutoff: Long): Int
}
