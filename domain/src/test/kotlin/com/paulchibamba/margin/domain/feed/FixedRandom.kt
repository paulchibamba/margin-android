package com.paulchibamba.margin.domain.feed

import kotlin.random.Random

class FixedRandom(private val double: Double = 0.0, private val offset: Int = 0) : Random() {
    override fun nextBits(bitCount: Int): Int = 0
    override fun nextDouble(): Double = double
    override fun nextInt(from: Int, until: Int): Int = (from + offset).coerceIn(from, until - 1)
}
