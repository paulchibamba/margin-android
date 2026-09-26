package com.paulchibamba.margin.navigation

import kotlinx.serialization.Serializable

sealed interface TabGraph {

    @Serializable
    data object Feed : TabGraph

    @Serializable
    data object Read : TabGraph
}
