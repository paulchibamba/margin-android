package com.paulchibamba.margin.domain.progress

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RewardTemplatesTest {
    private val validator = RewardValidator()
    private val sources = ValidationSources(RewardSeedFixtures.conceptTitles, RewardSeedFixtures.noteText)
    private val templatedKinds = RewardKind.all.filter { it.isReward && it != RewardKind.Quote }

    @Test
    fun `every reward kind but quote has three to five templates, and quote has its own titles`() {
        templatedKinds.forEach { kind -> assertTrue(RewardTemplates.of(kind).size in 3..5, "${kind.key} templates") }
        assertTrue(RewardTemplates.quoteTitles.size >= 3)
    }

    @Test
    fun `every template of every reward kind passes the validator on the fixture seeds`() {
        templatedKinds.forEach { kind ->
            val seeds = RewardSeedFixtures.seeds.getValue(kind)
            RewardTemplates.of(kind).forEachIndexed { index, template ->
                val drafts = seeds.mapNotNull { seed -> template.fill(seed.facts)?.let { draft -> seed to draft } }
                assertTrue(drafts.isNotEmpty(), "${kind.key} template $index fills no fixture seed")
                drafts.forEach { (seed, draft) ->
                    assertEquals(emptySet(), validator.rejectionsOf(draft, seed, sources), "${kind.key} $index: $draft")
                }
            }
        }
    }

    @Test
    fun `every quote title gives a valid verbatim quote with its source`() {
        val seed = RewardSeedFixtures.seeds.getValue(RewardKind.Quote).single()

        repeat(20) { randomSeed ->
            val draft = assertNotNull(TemplateWriter(Random(randomSeed)).write(seed, sources))

            assertTrue(draft.body in RewardSeedFixtures.noteText)
            assertEquals("Alice and Bob Learn Application Security · Least Privilege", draft.sourceLine)
            assertTrue(validator.isValid(draft, seed, sources))
        }
    }

    @Test
    fun `the writer only returns drafts that pass the validator`() {
        RewardSeedFixtures.seeds.values.flatten().forEach { seed ->
            repeat(10) { randomSeed ->
                val draft = assertNotNull(TemplateWriter(Random(randomSeed)).write(seed, sources))
                assertTrue(validator.isValid(draft, seed, sources))
            }
        }
    }

    @Test
    fun `a quote needs the note text, and re-explains have no template`() {
        val quote = RewardSeedFixtures.seeds.getValue(RewardKind.Quote).single()
        val reExplain = RewardSeed(RewardKind.ReExplain, listOf(leastPrivilege.id), facts = mapOf("concept" to "x"))

        assertNull(TemplateWriter(Random(1)).write(quote, ValidationSources()))
        assertNull(TemplateWriter(Random(1)).write(reExplain, sources))
    }

    @Test
    fun `a template whose title would be too long is passed over for one that fits`() {
        val longTitle = "A Very Long Concept Title That Keeps Going Past The Limit"
        val seed = RewardSeed(
            RewardKind.Comeback,
            listOf(leastPrivilege.id),
            facts = mapOf("concept" to longTitle, "date" to "25 Sep", "failCount" to "2", "passCount" to "3"),
        )

        repeat(10) { randomSeed ->
            val draft = assertNotNull(TemplateWriter(Random(randomSeed)).write(seed, ValidationSources()))
            assertTrue(draft.title.length <= RewardValidator.MAX_TITLE)
        }
    }
}
