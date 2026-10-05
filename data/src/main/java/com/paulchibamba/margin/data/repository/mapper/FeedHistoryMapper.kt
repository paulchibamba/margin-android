package com.paulchibamba.margin.data.repository.mapper

import com.paulchibamba.margin.data.database.entity.FeedHistoryEntity
import com.paulchibamba.margin.domain.feed.FeedHistoryEntry
import com.paulchibamba.margin.domain.model.ConceptId
import com.paulchibamba.margin.domain.model.Format
import com.paulchibamba.margin.domain.model.PostId
import com.paulchibamba.margin.domain.model.PostRole

object FeedHistoryMapper {

    fun toEntity(entry: FeedHistoryEntry) = FeedHistoryEntity(
        step = entry.step,
        postId = entry.postId.value,
        conceptId = entry.conceptId.value,
        format = FormatNames.nameOf(entry.format),
        role = entry.role.name.lowercase(),
        source = SourceNames.nameOf(entry.source),
    )

    fun toDomain(entity: FeedHistoryEntity) = FeedHistoryEntry(
        step = entity.step,
        postId = PostId(entity.postId),
        conceptId = ConceptId(entity.conceptId),
        format = FormatNames.formatOf(entity.format),
        role = PostRole.valueOf(entity.role.uppercase()),
        source = SourceNames.sourceOf(entity.source),
    )
}
