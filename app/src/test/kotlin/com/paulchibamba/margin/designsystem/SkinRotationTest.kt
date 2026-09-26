package com.paulchibamba.margin.designsystem

import org.junit.Test
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

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
    fun `the first draw can be any skin`() {
        val rotation = SkinRotation(Random(seed = 3))
        val firstDraws = (1..200).map { rotation.next(null) }.toSet()
        assertEquals(Skins.all.toSet(), firstDraws)
    }
}
