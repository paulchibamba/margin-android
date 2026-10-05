package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.repository.Clock
import com.paulchibamba.margin.domain.repository.ContentRepository
import com.paulchibamba.margin.domain.repository.RollupStore
import com.paulchibamba.margin.domain.rollup.AttentionSummary
import com.paulchibamba.margin.domain.rollup.DailyRollup
import com.paulchibamba.margin.domain.rollup.RereadHotspot
import com.paulchibamba.margin.domain.rollup.RereadSubject
import com.paulchibamba.margin.domain.rollup.TimeOfDay
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveAttention @Inject constructor(
    private val store: RollupStore,
    private val content: ContentRepository,
    private val clock: Clock,
) {

    operator fun invoke(): Flow<AttentionReport> {
        val today = clock.now().atZone(clock.zone()).toLocalDate()
        return store.observeFrom(today.minusDays(AttentionReport.DAYS - 1)).map { rollups ->
            reportOf(AttentionSummary.of(rollups.map(DailyRollup::metrics)))
        }
    }

    private suspend fun reportOf(summary: AttentionSummary) = AttentionReport(
        glanceRate = summary.glanceRate,
        deepRate = summary.deepRate,
        ratioByFormat = summary.ratioByFormat.toList(),
        hotspots = hotspotsOf(summary.hotspots),
        paceByBook = content.books().mapNotNull { book -> summary.paceByBook[book.slug]?.let { book to it } },
        paceByTimeOfDay = TimeOfDay.entries.mapNotNull { time -> summary.paceByTimeOfDay[time]?.let { time to it } },
    )

    private suspend fun hotspotsOf(hotspots: List<RereadHotspot>): List<Pair<String, Int>> {
        if (hotspots.isEmpty()) return emptyList()
        val titles = conceptTitles() + noteTitles()
        return hotspots.mapNotNull { hotspot -> titles[hotspot.subject]?.let { title -> title to hotspot.count } }
    }

    private suspend fun conceptTitles(): Map<RereadSubject, String> =
        content.concepts().associate { concept -> RereadSubject.OfConcept(concept.id) to concept.title }

    private suspend fun noteTitles(): Map<RereadSubject, String> = content.noteOutlines().associate { outline ->
        RereadSubject.OfNote(outline.id) to "Ch ${outline.position.chapter} · ${outline.section}"
    }
}
