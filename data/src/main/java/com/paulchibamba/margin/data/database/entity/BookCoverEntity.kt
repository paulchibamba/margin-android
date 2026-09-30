package com.paulchibamba.margin.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "book_cover")
data class BookCoverEntity(
    @PrimaryKey val bookSlug: String,
    val fileName: String,
    val updatedAt: Long,
)
