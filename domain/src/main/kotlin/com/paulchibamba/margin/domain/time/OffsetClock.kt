package com.paulchibamba.margin.domain.time

import com.paulchibamba.margin.domain.repository.Clock
import com.paulchibamba.margin.domain.repository.ClockOffsetStore
import java.time.Instant
import java.time.ZoneId
import kotlin.time.Duration
import kotlin.time.toJavaDuration

class OffsetClock(private val base: Clock, private val store: ClockOffsetStore) : Clock {

    @Volatile
    private var storedOffset: Duration? = null

    val offset: Duration
        get() = storedOffset ?: store.load().also { storedOffset = it }

    override fun now(): Instant = base.now().plus(offset.toJavaDuration())

    override fun zone(): ZoneId = base.zone()

    fun advance(by: Duration) = saveOffset(offset + by)

    fun reset() = saveOffset(Duration.ZERO)

    private fun saveOffset(offset: Duration) {
        store.save(offset)
        storedOffset = offset
    }
}
