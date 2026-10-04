package com.paulchibamba.margin.domain.repository

import com.paulchibamba.margin.domain.tracking.RecentEvent
import com.paulchibamba.margin.domain.tracking.UnfinishedSession

interface EventLog {
    suspend fun unfinishedSession(): UnfinishedSession?
    suspend fun recent(limit: Int): List<RecentEvent>
}
