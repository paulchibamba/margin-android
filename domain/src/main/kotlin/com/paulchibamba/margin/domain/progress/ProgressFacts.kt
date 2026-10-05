package com.paulchibamba.margin.domain.progress

import com.paulchibamba.margin.domain.model.Concept
import com.paulchibamba.margin.domain.model.ConceptId
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

data class ProgressFacts(
    val today: LocalDate,
    val zone: ZoneId,
    val chapters: List<ChapterProgress>,
    val introduced: List<Concept>,
    val introductions: List<Introduction>,
    val comebacks: List<Comeback>,
    val remembered: List<Concept>,
    val newlyRemembered: List<Concept>,
    val upcomingNotes: List<UpcomingNote>,
    val unquotedReadNotes: List<ReadNote>,
    val attention: AttentionFacts,
    val struggles: List<Struggle>,
    val anglesShown: Map<ConceptId, List<Angle>>,
    val relations: ConceptRelations,
) {
    val introducedToday: List<Introduction>
        get() = seenIntroductions().filter { introduction -> introduction.on == today }

    fun introducedAtLeastDaysAgo(days: Long): List<Introduction> =
        seenIntroductions().filter { introduction -> !introduction.on.isAfter(today.minusDays(days)) }

    fun knownConcepts(): List<Concept> = introduced.filterNot(attention::isGlancedOnly)

    fun dateOf(instant: Instant): LocalDate = instant.atZone(zone).toLocalDate()

    fun chapterOf(concept: Concept): ChapterProgress? = chapters.firstOrNull { chapter -> chapter.contains(concept) }

    private fun seenIntroductions(): List<Introduction> =
        introductions.filterNot { introduction -> attention.isGlancedOnly(introduction.concept) }
}
