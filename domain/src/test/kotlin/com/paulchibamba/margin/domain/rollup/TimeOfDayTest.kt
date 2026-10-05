package com.paulchibamba.margin.domain.rollup

import com.paulchibamba.margin.domain.rollup.TimeOfDay.AFTERNOON
import com.paulchibamba.margin.domain.rollup.TimeOfDay.EVENING
import com.paulchibamba.margin.domain.rollup.TimeOfDay.MORNING
import com.paulchibamba.margin.domain.rollup.TimeOfDay.NIGHT
import java.time.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals

class TimeOfDayTest {

    @Test
    fun `the day splits at five, noon, five and ten`() {
        val times = listOf("04:59", "05:00", "11:59", "12:00", "17:00", "21:59", "22:00", "23:59")
            .map { TimeOfDay.of(LocalTime.parse(it)) }

        val expected = listOf(NIGHT, MORNING, MORNING, AFTERNOON, EVENING, EVENING, NIGHT, NIGHT)
        assertEquals(expected, times)
    }
}
