package com.paulchibamba.margin.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.paulchibamba.margin.data.database.entity.ConceptProgressEntity

@Dao
interface ConceptProgressDao {

    @Upsert
    suspend fun upsert(progress: List<ConceptProgressEntity>)

    @Query("SELECT * FROM concept_progress")
    suspend fun all(): List<ConceptProgressEntity>

    @Query("SELECT * FROM concept_progress WHERE conceptId = :conceptId")
    suspend fun byId(conceptId: String): ConceptProgressEntity?
}
