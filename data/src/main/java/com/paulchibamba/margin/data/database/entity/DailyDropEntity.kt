package com.paulchibamba.margin.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_drop")
data class DailyDropEntity(
    @PrimaryKey val date: String,
    val composedAt: Long,
    val composedAtStep: Int,
    val headline: String?,
    val items: String,
    val position: Int,
    val completedAt: Long?,
    val continuedIntoFeed: Boolean,
)
