package com.paulchibamba.margin.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_post")
data class SavedPostEntity(
    @PrimaryKey val postId: String,
    val savedAt: Long,
)
