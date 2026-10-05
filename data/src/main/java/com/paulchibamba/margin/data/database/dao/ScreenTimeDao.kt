package com.paulchibamba.margin.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.paulchibamba.margin.data.database.entity.AppCategoryOverrideEntity
import com.paulchibamba.margin.data.database.entity.ScreenTimeDailyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScreenTimeDao {

    @Transaction
    suspend fun replaceDay(date: String, apps: List<ScreenTimeDailyEntity>) {
        deleteDay(date)
        insert(apps)
    }

    @Query("DELETE FROM screen_time_daily WHERE date = :date")
    suspend fun deleteDay(date: String)

    @Insert
    suspend fun insert(apps: List<ScreenTimeDailyEntity>)

    @Query("SELECT * FROM screen_time_daily WHERE date = :date")
    suspend fun appsOn(date: String): List<ScreenTimeDailyEntity>

    @Query("SELECT DISTINCT date FROM screen_time_daily ORDER BY date")
    suspend fun dates(): List<String>

    @Query("SELECT DISTINCT date FROM screen_time_daily WHERE packageName = :packageName ORDER BY date")
    suspend fun datesWith(packageName: String): List<String>

    @Query("DELETE FROM screen_time_daily")
    suspend fun deleteAll()

    @Query("SELECT * FROM app_category_override")
    suspend fun overrides(): List<AppCategoryOverrideEntity>

    @Transaction
    suspend fun setDoom(override: AppCategoryOverrideEntity) {
        upsertOverride(override)
        markDoom(override.packageName, override.isDoom)
    }

    @Upsert
    suspend fun upsertOverride(override: AppCategoryOverrideEntity)

    @Query("UPDATE screen_time_daily SET isDoom = :isDoom WHERE packageName = :packageName")
    suspend fun markDoom(packageName: String, isDoom: Boolean)

    @Query(
        """
        SELECT MAX(date) AS date, packageName, label, category, isDoom, SUM(foregroundMs) AS foregroundMs
        FROM screen_time_daily WHERE date >= :date
        GROUP BY packageName ORDER BY foregroundMs DESC
        """,
    )
    fun observeTotalsFrom(date: String): Flow<List<ScreenTimeDailyEntity>>
}
