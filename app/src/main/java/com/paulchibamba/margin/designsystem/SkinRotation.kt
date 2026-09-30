package com.paulchibamba.margin.designsystem

import kotlin.random.Random

class SkinRotation(
    private val random: Random,
    private val skins: List<Skin> = Skins.all,
) {
    fun next(previous: Skin?, isDarkOnly: Boolean = false): Skin =
        skins.filter { it != previous && (!isDarkOnly || !it.isLight) }.random(random)
}
