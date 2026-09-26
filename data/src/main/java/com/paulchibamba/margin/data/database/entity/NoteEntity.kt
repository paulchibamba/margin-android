package com.paulchibamba.margin.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "note", indices = [Index("bookSlug", "chapter", "order")])
data class NoteEntity(
    @PrimaryKey val id: String,
    val bookSlug: String,
    val chapter: Int,
    val order: Int,
    val section: String,
    val part: Int,
    val parts: Int,
    val html: String,
    val words: Int,
    val minutes: Double,
)
