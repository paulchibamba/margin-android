package com.paulchibamba.margin.domain.rollup

import com.paulchibamba.margin.domain.model.Format
import com.paulchibamba.margin.domain.tracking.Event
import kotlin.time.Duration.Companion.milliseconds

object PostRollup {
    val GLANCE_UNDER = 1500.milliseconds
    const val DEEP_RATIO = 0.8

    fun of(day: DayEvents): PostMetrics {
        val exposures = day.all<Event.PostExposure>()
        return PostMetrics(
            postsSeen = day.all<Event.PostImpression>().count { !it.isRevisit },
            exposures = exposures.size,
            glances = exposures.count { it.activeTime < GLANCE_UNDER },
            deepReads = exposures.count { it.attentionRatio >= DEEP_RATIO },
            ratioByFormat = ratioByFormatOf(day, exposures),
        )
    }

    private fun ratioByFormatOf(day: DayEvents, exposures: List<Event.PostExposure>): Map<Format, FormatAttention> =
        exposures
            .mapNotNull { exposure -> day.impressionsByPost[exposure.postId]?.let { it.format to exposure } }
            .groupBy({ (format, _) -> format }, { (_, exposure) -> exposure })
            .mapValues { (_, group) -> attentionOf(group) }
            .entries
            .sortedByDescending { it.value.medianRatio }
            .associate { it.key to it.value }

    private fun attentionOf(exposures: List<Event.PostExposure>): FormatAttention {
        val medianRatio = Median.of(exposures.map(Event.PostExposure::attentionRatio)) ?: 0.0
        return FormatAttention(medianRatio, exposures.size)
    }
}
