package com.paulchibamba.margin.data.database.entity

import androidx.room.Entity

@Entity(tableName = "reading_only_chapter", primaryKeys = ["bookSlug", "chapter"])
data class ReadingOnlyChapterEntity(
    val bookSlug: String,
    val chapter: Int,
)
