package com.paulchibamba.margin.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_category_override")
data class AppCategoryOverrideEntity(@PrimaryKey val packageName: String, val isDoom: Boolean)
