package com.paulchibamba.margin.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.paulchibamba.margin.data.database.entity.DailyRollupEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RollupDao {

    @Upsert
    suspend fun upsert(rollup: DailyRollupEntity)

    @Query("SELECT date, computedAt FROM daily_rollup")
    suspend fun stamps(): List<RollupStamp>

    @Query("SELECT * FROM daily_rollup WHERE date >= :date ORDER BY date")
    fun observeFrom(date: String): Flow<List<DailyRollupEntity>>
}
