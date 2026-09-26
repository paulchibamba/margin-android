package com.paulchibamba.margin.data.database.entity

import androidx.room.Entity

@Entity(tableName = "chapter", primaryKeys = ["bookSlug", "number"])
data class ChapterEntity(
    val bookSlug: String,
    val number: Int,
    val title: String,
)
