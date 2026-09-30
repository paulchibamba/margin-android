package com.paulchibamba.margin.designsystem

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
    fun `dark-only draws use Ink, Cobalt and Forest, never twice in a row`() {
        val rotation = SkinRotation(Random(seed = 5))
        val drawn = generateSequence(rotation.next(null, isDarkOnly = true)) { rotation.next(it, isDarkOnly = true) }
            .take(1000)
            .toList()

        assertEquals(setOf(Skins.Ink, Skins.Cobalt, Skins.Forest), drawn.toSet())
        drawn.zipWithNext().forEach { (previous, next) -> assertNotEquals(previous, next) }
    }

    @Test
    fun `a dark-only draw after a light skin is still dark`() {
        val rotation = SkinRotation(Random(seed = 9))
        val draws = (1..200).map { rotation.next(Skins.Paper, isDarkOnly = true) }

        assertTrue(draws.none { it.isLight })
    }

    @Test
    fun `the first draw can be any skin`() {
        val rotation = SkinRotation(Random(seed = 3))
        val firstDraws = (1..200).map { rotation.next(null) }.toSet()
        assertEquals(Skins.all.toSet(), firstDraws)
    }
}
