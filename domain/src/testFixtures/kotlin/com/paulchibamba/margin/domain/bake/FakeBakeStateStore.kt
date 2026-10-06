package com.paulchibamba.margin.domain.bake

import com.paulchibamba.margin.domain.repository.BakeStateStore

class FakeBakeStateStore(var state: BakeState = BakeState()) : BakeStateStore {
    override suspend fun load(): BakeState = state

    override suspend fun save(state: BakeState) {
        this.state = state
    }
}
