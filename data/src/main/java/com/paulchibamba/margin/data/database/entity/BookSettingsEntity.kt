package com.paulchibamba.margin.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "book_settings")
data class BookSettingsEntity(
    @PrimaryKey val bookSlug: String,
    val active: Boolean,
    val priority: String,
)
