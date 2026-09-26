package com.paulchibamba.margin.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "book")
data class BookEntity(
    @PrimaryKey val slug: String,
    val title: String,
    val position: Int,
)
