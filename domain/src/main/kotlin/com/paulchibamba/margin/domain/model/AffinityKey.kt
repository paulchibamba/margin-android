package com.paulchibamba.margin.domain.model

sealed interface AffinityKey {
    data class OfFormat(val format: Format) : AffinityKey

    data class OfRewardKind(val kind: String) : AffinityKey
}
