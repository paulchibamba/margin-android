package com.paulchibamba.margin.data.tracking

import com.paulchibamba.margin.data.database.MarginDatabase
import com.paulchibamba.margin.domain.repository.SessionTally
import com.paulchibamba.margin.domain.tracking.SessionCounts
import java.time.Instant
import javax.inject.Inject

class RoomSessionTally @Inject constructor(private val database: MarginDatabase) : SessionTally {

    override suspend fun countBetween(from: Instant, to: Instant): SessionCounts {
        val dao = database.sessionTallyDao()
        val window = from.toEpochMilli() to to.toEpochMilli()
        return SessionCounts(
            posts = dao.postsSeenBetween(window.first, window.second),
            notes = dao.notesReadBetween(window.first, window.second),
        )
    }
}
