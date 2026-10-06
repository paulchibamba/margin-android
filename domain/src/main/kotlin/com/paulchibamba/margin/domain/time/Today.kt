package com.paulchibamba.margin.domain.time

import com.paulchibamba.margin.domain.repository.Clock
import java.time.LocalDate

fun Clock.today(): LocalDate = now().atZone(zone()).toLocalDate()
