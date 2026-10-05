package com.paulchibamba.margin.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.paulchibamba.margin.data.database.entity.MetaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MetaDao {

    @Upsert
    suspend fun put(entries: List<MetaEntity>)

    @Query("SELECT value FROM meta WHERE `key` = :key")
    suspend fun get(key: String): String?

    @Query("SELECT value FROM meta WHERE `key` = :key")
    fun observe(key: String): Flow<String?>

    @Query("DELETE FROM meta WHERE `key` = :key")
    suspend fun delete(key: String)

    @Query("SELECT * FROM meta WHERE `key` LIKE :prefix || '%'")
    suspend fun withPrefix(prefix: String): List<MetaEntity>
}
