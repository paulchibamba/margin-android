package com.paulchibamba.margin.domain.screentime

import java.time.Instant

sealed interface UsageEvent {
    val at: Instant

    data class Resumed(override val at: Instant, val packageName: PackageName) : UsageEvent

    data class Paused(override val at: Instant, val packageName: PackageName) : UsageEvent

    data class NothingInFront(override val at: Instant) : UsageEvent
}
