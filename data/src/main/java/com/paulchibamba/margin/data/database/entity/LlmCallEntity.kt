package com.paulchibamba.margin.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "llm_call")
data class LlmCallEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val at: Long,
    val purpose: String,
    val model: String,
    val tokensIn: Long,
    val tokensCached: Long,
    val tokensOut: Long,
    val costMicros: Long,
    val ok: Boolean,
    val error: String?,
)
