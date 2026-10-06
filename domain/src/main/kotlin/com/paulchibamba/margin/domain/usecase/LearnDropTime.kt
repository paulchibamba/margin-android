package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.drop.DropTime
import com.paulchibamba.margin.domain.drop.LearnedSlot
import com.paulchibamba.margin.domain.repository.Clock
import com.paulchibamba.margin.domain.repository.DropTimeStore
import com.paulchibamba.margin.domain.repository.EventLog
import com.paulchibamba.margin.domain.repository.EventSink
import javax.inject.Inject

class LearnDropTime @Inject constructor(
    private val clock: Clock,
    private val sink: EventSink,
    private val log: EventLog,
    private val store: DropTimeStore,
) {

    suspend operator fun invoke(): LearnedSlot {
        sink.flush()
        val today = clock.now().atZone(clock.zone()).toLocalDate()
        val from = DropTime.windowStart(today).atStartOfDay(clock.zone()).toInstant()
        val until = today.plusDays(1).atStartOfDay(clock.zone()).toInstant()
        val starts = log.sessionStarts(from, until)
        val slot = DropTime.learnedSlot(starts, today, clock.zone())
        store.saveLearned(slot.start)
        return slot
    }
}
