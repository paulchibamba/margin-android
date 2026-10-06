package com.paulchibamba.margin.domain.bake

import kotlinx.serialization.Serializable

@Serializable
data class ModelRewardBatch(val posts: List<ModelRewardPost>, val headline: String = "")
