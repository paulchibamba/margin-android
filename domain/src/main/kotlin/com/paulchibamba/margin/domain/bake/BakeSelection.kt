package com.paulchibamba.margin.domain.bake

import com.paulchibamba.margin.domain.progress.RewardSeed

object BakeSelection {

    fun unbaked(seeds: List<RewardSeed>, bakedHashes: Set<String>): List<RewardSeed> =
        seeds.filterNot { seed -> seed.factsHash in bakedHashes }

    fun hasNewSeeds(rewardSeeds: List<RewardSeed>, state: BakeState): Boolean =
        rewardSeeds.any { seed -> seed.factsHash !in state.seenSeeds }
}
