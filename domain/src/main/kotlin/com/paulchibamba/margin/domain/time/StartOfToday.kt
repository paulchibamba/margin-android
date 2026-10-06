package com.paulchibamba.margin.domain.time

import com.paulchibamba.margin.domain.repository.Clock
import java.time.Instant

fun Clock.startOfToday(): Instant = now().atZone(zone()).toLocalDate().atStartOfDay(zone()).toInstant()
