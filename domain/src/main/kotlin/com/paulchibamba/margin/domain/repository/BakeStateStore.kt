package com.paulchibamba.margin.domain.repository

import com.paulchibamba.margin.domain.bake.BakeState

interface BakeStateStore {
    suspend fun load(): BakeState
    suspend fun save(state: BakeState)
}
