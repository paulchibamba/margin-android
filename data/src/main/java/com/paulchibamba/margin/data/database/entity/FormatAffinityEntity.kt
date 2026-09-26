package com.paulchibamba.margin.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "format_affinity")
data class FormatAffinityEntity(
    @PrimaryKey val format: String,
    val value: Double,
)
