package com.paulchibamba.margin.domain.progress

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RewardValidatorTest {
    private val validator = RewardValidator()
    private val sources = ValidationSources(RewardSeedFixtures.conceptTitles, RewardSeedFixtures.noteText)
    private val comeback = RewardSeed(
        RewardKind.Comeback,
        listOf(leastPrivilege.id),
        facts = mapOf("concept" to "Least Privilege", "date" to "25 Sep", "failCount" to "2", "passCount" to "3"),
    )
    private val quote = RewardSeed(RewardKind.Quote, listOf(leastPrivilege.id), facts = mapOf("section" to "Access"))
    private val reExplain =
        RewardSeed(RewardKind.ReExplain, listOf(leastPrivilege.id), facts = mapOf("concept" to "Least Privilege"))

    private fun rejectionsOf(
        title: String,
        body: String,
        seed: RewardSeed = comeback,
        from: ValidationSources = sources,
    ) = validator.rejectionsOf(RewardDraft(title, body), seed, from)

    @Test
    fun `HTML, markdown and entities are rejected as markup`() {
        val markup = listOf("<b>Bold</b>.", "See [this](https://example.com).", "**Loud**.", "Use `eval`.", "&lt;tag")
        markup.forEach { body -> assertEquals(setOf(Rejection.MARKUP), rejectionsOf("Comeback", body), body) }
        assertEquals(emptySet(), rejectionsOf("Comeback", "Plain words, even when a < b and b > a."))
    }

    @Test
    fun `a title of 60 characters passes and one of 61 is too long`() {
        assertEquals(emptySet(), rejectionsOf("a".repeat(60), "Fine."))
        assertEquals(setOf(Rejection.TITLE_TOO_LONG), rejectionsOf("a".repeat(61), "Fine."))
    }

    @Test
    fun `a body of 280 characters passes and one of 281 is too long`() {
        assertEquals(emptySet(), rejectionsOf("Fine", "a".repeat(280)))
        assertEquals(setOf(Rejection.BODY_TOO_LONG), rejectionsOf("Fine", "a".repeat(281)))
    }

    @Test
    fun `numbers and dates must come from the facts, written as digits or words`() {
        assertEquals(emptySet(), rejectionsOf("Comeback", "25 Sep: beat you 2×, now three times running."))
        assertEquals(setOf(Rejection.NUMBER_NOT_IN_FACTS), rejectionsOf("Comeback", "26 Sep: it beat you."))
        assertEquals(setOf(Rejection.NUMBER_NOT_IN_FACTS), rejectionsOf("Comeback", "Right four times running."))
    }

    @Test
    fun `a concept title must come from the facts`() {
        assertEquals(emptySet(), rejectionsOf("Least Privilege", "It stuck."))
        assertEquals(setOf(Rejection.TITLE_NOT_IN_FACTS), rejectionsOf("Least Privilege", "Like Defence in Depth."))
    }

    @Test
    fun `cheerleading, exclamation marks, emoji and claims about others are banned`() {
        listOf(
            "Great job on this.",
            "It stuck now!",
            "It stuck now 💪",
            "Better than 90% of developers.",
            "Most developers miss this.",
            "Don't worry, it is hard.",
            "Are you giving up on it?",
        ).forEach { body -> assertTrue(Rejection.BANNED_PATTERN in rejectionsOf("Comeback", body), body) }
    }

    @Test
    fun `a curious question that does not guilt is allowed, except in a re-explain`() {
        assertEquals(emptySet(), rejectionsOf("Comeback", "Which part held you back on 25 Sep?"))
        assertEquals(setOf(Rejection.BANNED_PATTERN), rejectionsOf("Re-explain", "What is it?", reExplain))
    }

    @Test
    fun `a quote must be a verbatim line of its note`() {
        val verbatim = "Least privilege means giving every account only the access it needs."

        assertEquals(emptySet(), rejectionsOf("From Access", verbatim, quote))
        val misquote = rejectionsOf("From Access", "Least privilege is good.", quote)
        assertEquals(setOf(Rejection.QUOTE_NOT_VERBATIM), misquote)
        val withoutNote = rejectionsOf("From Access", verbatim, quote, ValidationSources())
        assertEquals(setOf(Rejection.QUOTE_NOT_VERBATIM), withoutNote)
    }

    @Test
    fun `a model-written re-explain may not copy more than 12 words in a row from the excerpt`() {
        val model = sources.copy(isModelWritten = true)
        val pasted = "When a service account asks for admin rights just for testing, that is the moment to say no."
        val paraphrase = "Think of a service account like an app asking for CAMERA only when you tap scan."

        assertEquals(setOf(Rejection.COPIED_FROM_EXCERPT), rejectionsOf("Least access", pasted, reExplain, model))
        assertEquals(emptySet(), rejectionsOf("Least access", paraphrase, reExplain, model))
    }

    @Test
    fun `twelve copied words in a row are still allowed`() {
        val model = sources.copy(isModelWritten = true)
        val twelveWords = "Least privilege means giving every account only the access it needs, said simply."

        assertEquals(emptySet(), rejectionsOf("Least access", twelveWords, reExplain, model))
    }
}
