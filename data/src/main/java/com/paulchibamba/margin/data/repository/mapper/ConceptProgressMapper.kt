package com.paulchibamba.margin.data.repository.mapper

import com.paulchibamba.margin.data.database.entity.ConceptProgressEntity
import com.paulchibamba.margin.domain.feed.ConceptProgress
import com.paulchibamba.margin.domain.feed.Confidence
import com.paulchibamba.margin.domain.memory.CardState
import com.paulchibamba.margin.domain.memory.MemoryCard
import com.paulchibamba.margin.domain.model.ConceptId
import java.time.Instant

object ConceptProgressMapper {

    fun toEntity(concept: ConceptId, progress: ConceptProgress): ConceptProgressEntity {
        val card = progress.card
        return ConceptProgressEntity(
            conceptId = concept.value,
            introducedAtStep = progress.introducedAtStep,
            confidence = progress.confidence?.name?.lowercase(),
            fsrsDue = card?.due?.toEpochMilli(),
            fsrsStability = card?.stability ?: 0.0,
            fsrsDifficulty = card?.difficulty ?: 0.0,
            fsrsState = card?.state?.value ?: CardState.NEW.value,
            fsrsReps = card?.reps ?: 0,
            fsrsLapses = card?.lapses ?: 0,
            fsrsScheduledDays = card?.scheduledDays ?: 0,
            fsrsLearningSteps = card?.learningSteps ?: 0,
            fsrsLastReview = card?.lastReview?.toEpochMilli(),
            lostGraded = progress.isLostGraded,
            lostAtStep = progress.lostAtStep,
            retaughtAtStep = progress.retaughtAtStep,
            lastShownStep = progress.lastShownStep,
        )
    }

    fun toDomain(entity: ConceptProgressEntity): Pair<ConceptId, ConceptProgress> =
        ConceptId(entity.conceptId) to ConceptProgress(
            introducedAtStep = entity.introducedAtStep,
            card = cardOf(entity),
            confidence = entity.confidence?.let { Confidence.valueOf(it.uppercase()) },
            isLostGraded = entity.lostGraded,
            lostAtStep = entity.lostAtStep,
            retaughtAtStep = entity.retaughtAtStep,
            lastShownStep = entity.lastShownStep,
        )

    private fun cardOf(entity: ConceptProgressEntity): MemoryCard? {
        val due = entity.fsrsDue ?: return null
        return MemoryCard(
            due = Instant.ofEpochMilli(due),
            stability = entity.fsrsStability,
            difficulty = entity.fsrsDifficulty,
            scheduledDays = entity.fsrsScheduledDays,
            learningSteps = entity.fsrsLearningSteps,
            reps = entity.fsrsReps,
            lapses = entity.fsrsLapses,
            state = CardState.entries.single { it.value == entity.fsrsState },
            lastReview = entity.fsrsLastReview?.let(Instant::ofEpochMilli),
        )
    }
}
