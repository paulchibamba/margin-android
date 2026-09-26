package com.paulchibamba.margin.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.paulchibamba.margin.data.database.entity.ActionLogEntity
import com.paulchibamba.margin.data.database.entity.ReviewLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedLogDao {

    @Insert
    suspend fun insertAction(action: ActionLogEntity)

    @Insert
    suspend fun insertReview(review: ReviewLogEntity)

    @Query("SELECT * FROM action_log ORDER BY id")
    fun actions(): Flow<List<ActionLogEntity>>

    @Query("SELECT * FROM review_log ORDER BY id")
    fun reviews(): Flow<List<ReviewLogEntity>>
}
