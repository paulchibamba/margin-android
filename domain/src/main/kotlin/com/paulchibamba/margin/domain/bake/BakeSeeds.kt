package com.paulchibamba.margin.domain.bake

import com.paulchibamba.margin.domain.feed.NoteExcerpt
import com.paulchibamba.margin.domain.model.Concept
import com.paulchibamba.margin.domain.model.ConceptId
import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.progress.RewardSeed
import com.paulchibamba.margin.domain.progress.ValidationSources
import com.paulchibamba.margin.domain.repository.ContentRepository

class BakeSeeds private constructor(
    private val concepts: Map<ConceptId, Concept>,
    private val noteTexts: Map<NoteId, String>,
) {
    private val titles: Set<String> = concepts.values.map(Concept::title).toSet()

    fun of(seed: RewardSeed) = BakeSeed(
        seed = seed,
        sources = ValidationSources(titles, seed.noteId?.let(noteTexts::get)),
        conceptTitles = seed.conceptIds.mapNotNull { id -> concepts[id]?.title },
    )

    companion object {
        suspend fun load(content: ContentRepository, seeds: List<RewardSeed>): BakeSeeds {
            val notes = content.notes(seeds.mapNotNull(RewardSeed::noteId).distinct())
            return BakeSeeds(
                concepts = content.concepts().associateBy(Concept::id),
                noteTexts = notes.associate { note -> note.id to NoteExcerpt.plainText(note.html) },
            )
        }
    }
}
