package com.paulchibamba.margin.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.paulchibamba.margin.data.database.entity.LlmCallEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LlmCallDao {

    @Insert
    suspend fun insert(call: LlmCallEntity)

    @Query("SELECT * FROM llm_call ORDER BY id")
    suspend fun all(): List<LlmCallEntity>

    @Query(SPEND_SINCE)
    suspend fun spendSince(start: Long): LlmSpendRow

    @Query(SPEND_SINCE)
    fun observeSpendSince(start: Long): Flow<LlmSpendRow>

    private companion object {
        const val SPEND_SINCE =
            "SELECT COUNT(*) AS calls, COALESCE(SUM(costMicros), 0) AS costMicros FROM llm_call WHERE at >= :start"
    }
}
