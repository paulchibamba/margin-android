package com.paulchibamba.margin.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "action_log", indices = [Index("conceptId")])
data class ActionLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val at: Long,
    val step: Int,
    val postId: String,
    val conceptId: String,
    val action: String,
)
