package com.paulchibamba.margin.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "concept",
    indices = [Index("bookSlug", "chapter", "order"), Index("noteChapter", "noteOrder")],
)
data class ConceptEntity(
    @PrimaryKey val id: String,
    val bookSlug: String,
    val chapter: Int,
    val order: Int,
    val title: String,
    val summary: String,
    val section: String,
    val noteId: String?,
    val noteChapter: Int?,
    val noteOrder: Int?,
)
