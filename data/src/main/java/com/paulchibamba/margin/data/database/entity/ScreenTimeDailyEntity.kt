package com.paulchibamba.margin.data.database.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(tableName = "screen_time_daily", primaryKeys = ["date", "packageName"], indices = [Index("date")])
data class ScreenTimeDailyEntity(
    val date: String,
    val packageName: String,
    val label: String,
    val category: String,
    val isDoom: Boolean,
    val foregroundMs: Long,
)
