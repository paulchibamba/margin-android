package com.paulchibamba.margin.designsystem

import com.paulchibamba.margin.domain.model.FeedTone
import kotlin.random.Random

class SkinRotation(private val random: Random) {

    fun next(previous: Skin?, tone: FeedTone = FeedTone.ANY): Skin =
        Skins.forTone(tone).filter { it != previous }.random(random)
}
