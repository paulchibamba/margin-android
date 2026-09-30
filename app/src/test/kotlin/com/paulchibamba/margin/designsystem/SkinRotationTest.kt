package com.paulchibamba.margin.designsystem

import com.paulchibamba.margin.domain.model.FeedTone
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import org.junit.Test

class SkinRotationTest {

    @Test
    fun `never returns the same skin twice in a row over 1000 draws`() {
        val rotation = SkinRotation(Random(seed = 14))
        var previous: Skin? = null
        repeat(1000) {
            val next = rotation.next(previous)
            assertNotEquals(previous, next)
            previous = next
        }
    }

    @Test
    fun `draws every skin over 1000 draws`() {
        val rotation = SkinRotation(Random(seed = 7))
        val drawn = generateSequence(rotation.next(null)) { rotation.next(it) }.take(1000).toSet()
        assertEquals(Skins.all.toSet(), drawn)
    }

    @Test
    fun `dark draws use Ink, Cobalt and Forest, never twice in a row`() {
        val drawn = drawsOf(FeedTone.DARK, seed = 5)

        assertEquals(setOf(Skins.Ink, Skins.Cobalt, Skins.Forest), drawn.toSet())
        drawn.zipWithNext().forEach { (previous, next) -> assertNotEquals(previous, next) }
    }

    @Test
    fun `night draws are always Ink, even straight after Ink`() {
        val drawn = drawsOf(FeedTone.NIGHT, seed = 11)

        assertEquals(setOf(Skins.Ink), drawn.toSet())
    }

    @Test
    fun `a dark or night draw after a light skin is never light`() {
        val rotation = SkinRotation(Random(seed = 9))
        val draws = (1..200).flatMap { listOf(FeedTone.DARK, FeedTone.NIGHT).map { rotation.next(Skins.Paper, it) } }

        assertTrue(draws.none { it.isLight })
    }

    private fun drawsOf(tone: FeedTone, seed: Int): List<Skin> {
        val rotation = SkinRotation(Random(seed))
        return generateSequence(rotation.next(null, tone)) { rotation.next(it, tone) }.take(1000).toList()
    }

    @Test
    fun `the first draw can be any skin`() {
        val rotation = SkinRotation(Random(seed = 3))
        val firstDraws = (1..200).map { rotation.next(null) }.toSet()
        assertEquals(Skins.all.toSet(), firstDraws)
    }
}
