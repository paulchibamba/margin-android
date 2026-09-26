package com.paulchibamba.margin.designsystem

import kotlin.random.Random

class SkinRotation(
    private val random: Random,
    private val skins: List<Skin> = Skins.all,
) {
    fun next(previous: Skin?): Skin = skins.filter { it != previous }.random(random)
}
