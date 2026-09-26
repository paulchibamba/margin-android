package com.paulchibamba.margin.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note_read")
data class NoteReadEntity(
    @PrimaryKey val noteId: String,
    val readAt: Long,
)
