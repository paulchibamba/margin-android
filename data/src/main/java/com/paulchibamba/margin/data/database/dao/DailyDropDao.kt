package com.paulchibamba.margin.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.paulchibamba.margin.data.database.entity.DailyDropEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyDropDao {

    @Query("SELECT * FROM daily_drop WHERE date = :date")
    suspend fun forDate(date: String): DailyDropEntity?

    @Query("SELECT * FROM daily_drop WHERE date = :date")
    fun observe(date: String): Flow<DailyDropEntity?>

    @Upsert
    suspend fun save(drop: DailyDropEntity)
}
