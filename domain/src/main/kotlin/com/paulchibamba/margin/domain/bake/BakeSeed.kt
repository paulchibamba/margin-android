package com.paulchibamba.margin.domain.bake

import com.paulchibamba.margin.domain.progress.RewardSeed
import com.paulchibamba.margin.domain.progress.ValidationSources

data class BakeSeed(
    val seed: RewardSeed,
    val sources: ValidationSources,
    val conceptTitles: List<String> = emptyList(),
) {
    val id: String get() = seed.factsHash
}
