package com.paulchibamba.margin.domain.progress.rules

import com.paulchibamba.margin.domain.progress.FactKey
import com.paulchibamba.margin.domain.progress.ProgressFacts
import com.paulchibamba.margin.domain.progress.ReadNote
import com.paulchibamba.margin.domain.progress.RewardHistory
import com.paulchibamba.margin.domain.progress.RewardKind
import com.paulchibamba.margin.domain.progress.RewardRule
import com.paulchibamba.margin.domain.progress.RewardSeed

class QuoteRule : RewardRule {

    override fun seedsFrom(facts: ProgressFacts, history: RewardHistory): List<RewardSeed> =
        facts.unquotedReadNotes.filterNot { read -> read.note.id in history.quotedNotes() }.map(::seedOf)

    private fun seedOf(read: ReadNote) = RewardSeed(
        kind = RewardKind.Quote,
        conceptIds = read.concepts.map { concept -> concept.id },
        noteId = read.note.id,
        facts = mapOf(FactKey.BOOK to read.bookTitle, FactKey.SECTION to read.note.section),
    )
}
