package com.paulchibamba.margin.domain.bake

import kotlinx.serialization.Serializable

@Serializable
data class ModelRewardPost(val seedId: String, val title: String, val body: String, val quote: String? = null)
