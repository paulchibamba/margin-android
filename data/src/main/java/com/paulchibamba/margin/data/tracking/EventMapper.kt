package com.paulchibamba.margin.data.tracking

import com.paulchibamba.margin.data.database.entity.EventEntity
import com.paulchibamba.margin.domain.tracking.LoggedEvent
import com.paulchibamba.margin.domain.tracking.RecentEvent
import java.time.Instant

object EventMapper {
    const val SCHEMA_VERSION = 1

    fun toEntity(logged: LoggedEvent) = EventEntity(
        at = logged.at.toEpochMilli(),
        sessionId = logged.sessionId?.value,
        type = logged.event.type.key,
        subjectId = EventProps.subjectOf(logged.event),
        props = EventProps.of(logged.event).toString(),
        schemaVersion = SCHEMA_VERSION,
    )

    fun toRecent(entity: EventEntity) = RecentEvent(
        at = Instant.ofEpochMilli(entity.at),
        typeKey = entity.type,
        subjectId = entity.subjectId,
        props = entity.props,
    )
}
