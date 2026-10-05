package com.paulchibamba.margin.domain.progress

import com.paulchibamba.margin.domain.progress.rules.MilestoneRule
import com.paulchibamba.margin.domain.progress.rules.NowYouCanRule

object RewardSeedFixtures {
    private const val BOOK = "Alice and Bob Learn Application Security"
    private const val CHAPTER = "CHAPTER 4: Secure Code"

    val conceptTitles: Set<String> = allConcepts.map { it.title }.toSet()

    val noteText = "Least privilege means giving every account only the access it needs. " +
        "When a service account asks for admin rights just for testing, that is the moment to say no. " +
        "Access that is never granted can never be abused by an attacker who takes over the account."

    private fun seed(kind: RewardKind, vararg facts: Pair<String, String>) =
        RewardSeed(kind, listOf(leastPrivilege.id), facts = mapOf(*facts))

    private fun comingUp(notesAway: String) = seed(
        RewardKind.ComingUp,
        FactKey.CONCEPT to needToKnow.title,
        FactKey.KNOWN_CONCEPT to leastPrivilege.title,
        FactKey.NOTES_AWAY to notesAway,
        FactKey.SECTION to "Need to Know",
    )

    private val chapterFacts = arrayOf(
        FactKey.BOOK to BOOK,
        FactKey.CHAPTER to CHAPTER,
        FactKey.TOTAL to "12",
        FactKey.CONCEPTS to "Input Validation; Output Encoding; Parameterized Queries",
    )

    val seeds: Map<RewardKind, List<RewardSeed>> = mapOf(
        RewardKind.ZoomOut to listOf(
            seed(RewardKind.ZoomOut, *chapterFacts, FactKey.INTRODUCED to "9", FactKey.REMAINING to "3",
                FactKey.NEXT to "Least Privilege", FactKey.THRESHOLD to "50%"),
            seed(RewardKind.ZoomOut, *chapterFacts, FactKey.INTRODUCED to "12", FactKey.REMAINING to "0",
                FactKey.THRESHOLD to "100%"),
            seed(RewardKind.ZoomOut, *chapterFacts, FactKey.INTRODUCED to "4", FactKey.REMAINING to "8",
                FactKey.NEXT to "Defence in Depth", FactKey.REREAD_CONCEPT to "The CIA Triad"),
        ),
        RewardKind.ComingUp to listOf(
            comingUp(notesAway = "3"),
            comingUp(notesAway = "1"),
        ),
        RewardKind.Callback to listOf(
            seed(RewardKind.Callback, FactKey.CONCEPT to needToKnow.title,
                FactKey.OLDER_CONCEPT to leastPrivilege.title, FactKey.DAYS_AGO to "12"),
        ),
        RewardKind.Comeback to listOf(
            seed(RewardKind.Comeback, FactKey.CONCEPT to leastPrivilege.title, FactKey.DATE to "25 Sep",
                FactKey.FAIL_COUNT to "2", FactKey.PASS_COUNT to "3"),
        ),
        RewardKind.NowYouCan to listOf(
            seed(RewardKind.NowYouCan, FactKey.CONCEPT to leastPrivilege.title, FactKey.SUMMARY to "Grant the least.",
                FactKey.WIN to NowYouCanRule.WIN_REMEMBERED),
            seed(RewardKind.NowYouCan, FactKey.CONCEPT to leastPrivilege.title, FactKey.SUMMARY to "Grant the least.",
                FactKey.WIN to NowYouCanRule.WIN_COMEBACK),
        ),
        RewardKind.Milestone to listOf(
            seed(RewardKind.Milestone, FactKey.SCOPE to MilestoneRule.SCOPE_CHAPTER, FactKey.NAME to CHAPTER,
                FactKey.BOOK to BOOK, FactKey.MILESTONE to MilestoneRule.KIND_INTRODUCED, FactKey.INTRODUCED to "12",
                FactKey.REMEMBERED to "4", FactKey.DAYS to "19"),
            seed(RewardKind.Milestone, FactKey.SCOPE to MilestoneRule.SCOPE_BOOK, FactKey.NAME to BOOK,
                FactKey.BOOK to BOOK, FactKey.MILESTONE to MilestoneRule.KIND_REMEMBERED, FactKey.INTRODUCED to "36",
                FactKey.REMEMBERED to "36", FactKey.DAYS to "60"),
        ),
        RewardKind.Quote to listOf(
            RewardSeed(RewardKind.Quote, listOf(leastPrivilege.id), leastPrivilege.sourceNoteId,
                mapOf(FactKey.BOOK to BOOK, FactKey.SECTION to "Least Privilege")),
        ),
    )
}
