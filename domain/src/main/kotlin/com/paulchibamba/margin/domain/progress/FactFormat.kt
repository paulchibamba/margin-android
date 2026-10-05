package com.paulchibamba.margin.domain.progress

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object FactFormat {
    private val dayAndMonth = DateTimeFormatter.ofPattern("d MMM", Locale.ENGLISH)

    fun date(date: LocalDate): String = dayAndMonth.format(date)
}
