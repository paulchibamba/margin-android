package com.paulchibamba.margin.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "post", indices = [Index("conceptId")])
data class PostEntity(
    @PrimaryKey val id: String,
    val conceptId: String,
    val format: String,
    val role: String,
    val position: Int,
    val json: String,
)
