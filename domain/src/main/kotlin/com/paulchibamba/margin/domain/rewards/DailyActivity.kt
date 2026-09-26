package com.paulchibamba.margin.domain.rewards

import java.time.LocalDate

data class DailyActivity(
    val date: LocalDate,
    val postsSeen: Int = 0,
    val notesRead: Int = 0,
)
