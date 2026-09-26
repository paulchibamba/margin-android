package com.paulchibamba.margin.data.database.entity

import androidx.room.Entity

@Entity(tableName = "chapter_known", primaryKeys = ["bookSlug", "chapter"])
data class ChapterKnownEntity(
    val bookSlug: String,
    val chapter: Int,
    val markedAt: Long,
)
