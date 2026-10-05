package com.paulchibamba.margin.domain.repository

import com.paulchibamba.margin.domain.tracking.EventType
import com.paulchibamba.margin.domain.tracking.LoggedEvent
import com.paulchibamba.margin.domain.tracking.RecentEvent
import com.paulchibamba.margin.domain.tracking.UnfinishedSession
import java.time.Instant

interface EventLog {
    suspend fun unfinishedSession(): UnfinishedSession?
    suspend fun recent(limit: Int): List<RecentEvent>
    suspend fun countOf(type: EventType, subjectId: String): Int
    suspend fun between(from: Instant, until: Instant): List<LoggedEvent>
    suspend fun firstEventAt(): Instant?
    suspend fun deleteBefore(cutoff: Instant): Int
}
