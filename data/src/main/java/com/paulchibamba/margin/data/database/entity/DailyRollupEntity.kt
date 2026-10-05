package com.paulchibamba.margin.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_rollup")
data class DailyRollupEntity(
    @PrimaryKey val date: String,
    val metrics: String,
    val computedAt: Long,
    val schemaVersion: Int,
)
