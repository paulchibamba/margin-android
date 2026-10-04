package com.paulchibamba.margin.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "event_log", indices = [Index("at"), Index("type", "at"), Index("subjectId")])
data class EventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val at: Long,
    val sessionId: String?,
    val type: String,
    val subjectId: String?,
    val props: String,
    val schemaVersion: Int,
)
