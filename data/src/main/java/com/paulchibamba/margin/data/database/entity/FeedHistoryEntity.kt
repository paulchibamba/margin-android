package com.paulchibamba.margin.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "feed_history")
data class FeedHistoryEntity(
    @PrimaryKey val step: Int,
    val postId: String,
    val conceptId: String,
    val format: String,
    val role: String,
    val source: String,
)
