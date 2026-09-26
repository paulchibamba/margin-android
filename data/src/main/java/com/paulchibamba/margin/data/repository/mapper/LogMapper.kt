package com.paulchibamba.margin.data.repository.mapper

import com.paulchibamba.margin.data.database.entity.ActionLogEntity
import com.paulchibamba.margin.data.database.entity.ReviewLogEntity
import com.paulchibamba.margin.domain.actions.ActionLogEntry
import com.paulchibamba.margin.domain.actions.PostAction
import com.paulchibamba.margin.domain.feed.ReviewLogEntry
import com.paulchibamba.margin.domain.memory.CardState
import com.paulchibamba.margin.domain.memory.Rating
import com.paulchibamba.margin.domain.model.ConceptId
import com.paulchibamba.margin.domain.model.PostId
import java.time.Instant
import kotlin.time.Duration.Companion.milliseconds

object LogMapper {

    fun toEntity(entry: ActionLogEntry) = ActionLogEntity(
        at = entry.at.toEpochMilli(),
        step = entry.step,
        postId = entry.postId.value,
        conceptId = entry.conceptId.value,
        action = entry.action.name.lowercase(),
    )

    fun toDomain(entity: ActionLogEntity) = ActionLogEntry(
        at = Instant.ofEpochMilli(entity.at),
        step = entity.step,
        postId = PostId(entity.postId),
        conceptId = ConceptId(entity.conceptId),
        action = PostAction.valueOf(entity.action.uppercase()),
    )

    fun toEntity(entry: ReviewLogEntry) = ReviewLogEntity(
        conceptId = entry.conceptId.value,
        postId = entry.postId.value,
        at = entry.at.toEpochMilli(),
        rating = entry.rating.value,
        stateBefore = entry.stateBefore.value,
        stabilityBefore = entry.stabilityBefore,
        stabilityAfter = entry.stabilityAfter,
        dwellMs = entry.dwell?.inWholeMilliseconds,
    )

    fun toDomain(entity: ReviewLogEntity) = ReviewLogEntry(
        at = Instant.ofEpochMilli(entity.at),
        conceptId = ConceptId(entity.conceptId),
        postId = PostId(entity.postId),
        rating = Rating.entries.single { it.value == entity.rating },
        stateBefore = CardState.entries.single { it.value == entity.stateBefore },
        stabilityBefore = entity.stabilityBefore,
        stabilityAfter = entity.stabilityAfter,
        dwell = entity.dwellMs?.milliseconds,
    )
}
