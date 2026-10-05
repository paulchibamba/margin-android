package com.paulchibamba.margin.domain.feed

import kotlin.random.Random

class RewardSchedule(private val every: IntRange, private val random: Random) {

    fun nextAfter(step: Int): Int = step + random.nextInt(every.first, every.last + 1)
}
