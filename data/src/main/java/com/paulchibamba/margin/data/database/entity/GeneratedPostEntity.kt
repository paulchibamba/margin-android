package com.paulchibamba.margin.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "generated_post")
data class GeneratedPostEntity(
    @PrimaryKey val id: String,
    val kind: String,
    val conceptIds: String,
    val noteId: String?,
    val title: String,
    val body: String,
    val sourceLine: String?,
    val factsJson: String,
    val factsHash: String,
    val writer: String,
    val createdAt: Long,
    val expiresAt: Long?,
    val shownAt: Long?,
    val lessPressed: Boolean,
)
