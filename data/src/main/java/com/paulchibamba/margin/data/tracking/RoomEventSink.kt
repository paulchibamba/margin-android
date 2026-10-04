package com.paulchibamba.margin.data.tracking

import com.paulchibamba.margin.data.database.MarginDatabase
import com.paulchibamba.margin.domain.repository.EventSink
import com.paulchibamba.margin.domain.tracking.LoggedEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class RoomEventSink(
    private val database: MarginDatabase,
    private val scope: CoroutineScope,
    private val batchSize: Int = BATCH_SIZE,
) : EventSink {
    private val buffer = mutableListOf<LoggedEvent>()

    override fun append(event: LoggedEvent) {
        val isFull = synchronized(buffer) {
            buffer += event
            buffer.size >= batchSize
        }
        if (isFull) scope.launch { flush() }
    }

    override suspend fun flush() {
        val batch = takeBuffered()
        if (batch.isNotEmpty()) database.eventDao().insertAll(batch.map(EventMapper::toEntity))
    }

    private fun takeBuffered(): List<LoggedEvent> = synchronized(buffer) {
        buffer.toList().also { buffer.clear() }
    }

    private companion object {
        const val BATCH_SIZE = 20
    }
}
