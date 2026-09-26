package com.paulchibamba.margin.domain.memory

import kotlin.math.roundToLong

private const val EIGHT_DECIMALS = 1e8

internal fun Double.roundedToEightDecimals(): Double = (this * EIGHT_DECIMALS).roundToLong() / EIGHT_DECIMALS
