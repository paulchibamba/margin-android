package com.paulchibamba.margin.data.database

data class NoteOutlineRow(
    val id: String,
    val bookSlug: String,
    val chapter: Int,
    val order: Int,
    val section: String,
    val minutes: Double,
)
