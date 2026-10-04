package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.repository.EventLog
import com.paulchibamba.margin.domain.repository.EventSink
import com.paulchibamba.margin.domain.tracking.RecentEvent
import javax.inject.Inject

class GetRecentEvents @Inject constructor(private val sink: EventSink, private val log: EventLog) {

    suspend operator fun invoke(limit: Int = DEFAULT_LIMIT): List<RecentEvent> {
        sink.flush()
        return log.recent(limit)
    }

    private companion object {
        const val DEFAULT_LIMIT = 12
    }
}
