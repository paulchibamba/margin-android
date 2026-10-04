package com.paulchibamba.margin.feature.debug

import com.paulchibamba.margin.domain.tracking.RecentEvent
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object RecentEventLabels {
    private val time = DateTimeFormatter.ofPattern("HH:mm:ss")

    fun lineFor(event: RecentEvent, zone: ZoneId): String {
        val at = time.format(event.at.atZone(zone))
        val subject = event.subjectId?.let { " $it" }.orEmpty()
        return "$at ${event.typeKey}$subject ${event.props}"
    }
}
