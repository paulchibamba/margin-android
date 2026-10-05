package com.paulchibamba.margin.domain.rollup

import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.model.Format

data class AttentionSummary(
    val glanceRate: Double?,
    val deepRate: Double?,
    val ratioByFormat: Map<Format, Double>,
    val hotspots: List<RereadHotspot>,
    val paceByBook: Map<BookSlug, Int>,
    val paceByTimeOfDay: Map<TimeOfDay, Int>,
) {
    companion object {
        fun of(days: List<RollupMetrics>): AttentionSummary {
            val posts = days.map(RollupMetrics::posts)
            val exposures = posts.sumOf(PostMetrics::exposures)
            return AttentionSummary(
                glanceRate = Share.of(posts.sumOf(PostMetrics::glances), exposures),
                deepRate = Share.of(posts.sumOf(PostMetrics::deepReads), exposures),
                ratioByFormat = ratioByFormatOf(posts),
                hotspots = hotspotsOf(days.map(RollupMetrics::rereads)),
                paceByBook = wordsPerMinuteOf(days.map { it.reading.paceByBook }),
                paceByTimeOfDay = wordsPerMinuteOf(days.map { it.reading.paceByTimeOfDay }),
            )
        }

        private fun ratioByFormatOf(posts: List<PostMetrics>): Map<Format, Double> = posts
            .flatMap { it.ratioByFormat.entries }
            .groupBy({ it.key }, { it.value })
            .mapValues { (_, days) -> weightedRatioOf(days) }
            .entries
            .sortedByDescending { it.value }
            .associate { it.key to it.value }

        private fun weightedRatioOf(days: List<FormatAttention>): Double =
            days.sumOf { it.medianRatio * it.exposures } / days.sumOf(FormatAttention::exposures)

        private fun hotspotsOf(rereads: List<RereadMetrics>): List<RereadHotspot> = rereads
            .flatMap(RereadMetrics::hotspots)
            .groupBy(RereadHotspot::subject)
            .map { (subject, days) -> RereadHotspot(subject, days.sumOf(RereadHotspot::count)) }
            .sortedByDescending(RereadHotspot::count)
            .take(RereadRollup.HOTSPOTS)

        private fun <K> wordsPerMinuteOf(days: List<Map<K, ReadingPace>>): Map<K, Int> = days
            .flatMap { it.entries }
            .groupBy({ it.key }, { it.value })
            .mapNotNull { (key, paces) -> paces.reduce(ReadingPace::plus).wordsPerMinute?.let { key to it } }
            .toMap()
    }
}
