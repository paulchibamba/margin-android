package com.paulchibamba.margin.domain.repository

import java.time.Instant
import java.time.ZoneId

interface Clock {
    fun now(): Instant
    fun zone(): ZoneId
}
