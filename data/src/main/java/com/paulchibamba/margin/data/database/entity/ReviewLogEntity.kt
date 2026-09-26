package com.paulchibamba.margin.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "review_log", indices = [Index("conceptId", "at")])
data class ReviewLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val conceptId: String,
    val postId: String,
    val at: Long,
    val rating: Int,
    val stateBefore: Int,
    val stabilityBefore: Double,
    val stabilityAfter: Double,
    val dwellMs: Long?,
)
