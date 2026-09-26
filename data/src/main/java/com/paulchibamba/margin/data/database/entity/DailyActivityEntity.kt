package com.paulchibamba.margin.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_activity")
data class DailyActivityEntity(
    @PrimaryKey val date: String,
    val postsSeen: Int,
    val notesRead: Int,
)
