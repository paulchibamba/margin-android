package com.paulchibamba.margin.domain.usecase

import java.time.LocalDate
import kotlin.time.Duration

data class ScreenTimeDay(val date: LocalDate, val margin: Duration, val doom: Duration)
