package com.paulchibamba.margin.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "post_seen")
data class PostSeenEntity(
    @PrimaryKey val postId: String,
    val firstSeenAt: Long,
    val lastSeenAt: Long,
    val lastSeenStep: Int,
    val times: Int,
    val lastDwellMs: Long?,
    val lastEngagement: Double?,
    val lastCorrect: Boolean?,
    val lastSource: String,
)
