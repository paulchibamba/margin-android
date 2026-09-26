package com.paulchibamba.margin.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "concept_progress")
data class ConceptProgressEntity(
    @PrimaryKey val conceptId: String,
    val introducedAtStep: Int?,
    val confidence: String?,
    val fsrsDue: Long?,
    val fsrsStability: Double,
    val fsrsDifficulty: Double,
    val fsrsState: Int,
    val fsrsReps: Int,
    val fsrsLapses: Int,
    val fsrsScheduledDays: Int,
    val fsrsLearningSteps: Int,
    val fsrsLastReview: Long?,
    val lostGraded: Boolean,
    val lostAtStep: Int?,
    val retaughtAtStep: Int?,
    val lastShownStep: Int?,
)
