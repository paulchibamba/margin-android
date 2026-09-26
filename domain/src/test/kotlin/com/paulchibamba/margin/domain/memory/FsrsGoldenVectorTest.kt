package com.paulchibamba.margin.domain.memory

import org.junit.Assume.assumeTrue
import java.io.File
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

class FsrsGoldenVectorTest {

    private val scheduler = FsrsScheduler(FsrsParameters.Default, NoFuzz)

    @Test
    fun `every step of every golden case matches ts-fsrs`() {
        val cases = GoldenVectors.load(goldenFileOrSkip())

        assertEquals(5, cases.size, "expected the five golden cases")
        cases.forEach(::replay)
    }

    private fun replay(case: GoldenCase) {
        var card = scheduler.createEmptyCard(case.steps.first().reviewAt)
        case.steps.forEachIndexed { index, step ->
            card = scheduler.next(card, step.reviewAt, step.rating)
            assertStepMatches(card, step, label = "${case.name} step ${index + 1} (${step.rating})")
        }
    }

    private fun assertStepMatches(card: MemoryCard, expected: GoldenStep, label: String) {
        assertEquals(expected.due, card.due, "$label due")
        assertEquals(expected.state, card.state, "$label state")
        assertEquals(expected.stability, card.stability, GOLDEN_PRECISION, "$label stability")
        assertEquals(expected.difficulty, card.difficulty, GOLDEN_PRECISION, "$label difficulty")
        assertEquals(expected.reps, card.reps, "$label reps")
        assertEquals(expected.lapses, card.lapses, "$label lapses")
        assertEquals(expected.scheduledDays, card.scheduledDays, "$label scheduled days")
        assertEquals(expected.learningSteps, card.learningSteps, "$label learning steps")
        assertRetrievabilityAtDue(card, expected.retrievabilityAtDue, label)
    }

    private fun assertRetrievabilityAtDue(card: MemoryCard, expected: Double, label: String) {
        val atDue: Instant = card.due
        assertEquals(expected, scheduler.retrievability(card, atDue), GOLDEN_PRECISION, "$label retrievability at due")
    }

    private fun goldenFileOrSkip(): File {
        val file = GoldenVectors.file
        assumeTrue("Skipped: fsrs-golden.json not found (docs/engineering is local only)", file != null)
        return file!!
    }

    private companion object {
        const val GOLDEN_PRECISION = 1e-6
    }
}
