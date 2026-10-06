package com.paulchibamba.margin.feature.drop

import com.paulchibamba.margin.domain.drop.DropCompletion
import com.paulchibamba.margin.domain.drop.DropEvidence
import com.paulchibamba.margin.domain.drop.DropSession
import com.paulchibamba.margin.domain.drop.DropStatus
import com.paulchibamba.margin.domain.feed.FeedItem
import kotlinx.coroutines.flow.MutableStateFlow

class FakeDropUseCases(var session: DropSession? = null) : DropUseCases {
    val status = MutableStateFlow<DropStatus?>(null)
    val entered = mutableListOf<Int>()
    val left = mutableListOf<Int>()
    var prepareCalls = 0
    var continued = 0
    var dismissed = 0
    var completion = DropCompletion(streak = 4, evidence = DropEvidence.Remembered(2))

    override fun observeToday() = status

    override suspend fun prepareToday() {
        prepareCalls++
    }

    override suspend fun open() = session

    override suspend fun enter(index: Int, item: FeedItem): FeedItem {
        entered += index
        return item.copy(step = 100 + index)
    }

    override suspend fun leave(index: Int): DropStatus? {
        left += index
        return null
    }

    override suspend fun continueIntoFeed() {
        continued++
    }

    override suspend fun dismiss() {
        dismissed++
    }

    override suspend fun completion() = completion
}
