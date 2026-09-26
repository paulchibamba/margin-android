package com.paulchibamba.margin.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.paulchibamba.margin.data.database.entity.DailyActivityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {

    @Upsert
    suspend fun upsert(activity: DailyActivityEntity)

    @Query("SELECT * FROM daily_activity WHERE date = :date")
    suspend fun on(date: String): DailyActivityEntity?

    @Query("SELECT * FROM daily_activity ORDER BY date")
    fun all(): Flow<List<DailyActivityEntity>>
}
