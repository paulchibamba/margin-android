package com.paulchibamba.margin.feature.drop

import com.paulchibamba.margin.data.startup.StartupInitializer
import com.paulchibamba.margin.domain.feed.Candidate
import com.paulchibamba.margin.domain.feed.FeedItem
import com.paulchibamba.margin.domain.usecase.DescribeDropCompletion
import com.paulchibamba.margin.domain.usecase.EndDropVisit
import com.paulchibamba.margin.domain.usecase.EnterDropItem
import com.paulchibamba.margin.domain.usecase.LeaveDropItem
import com.paulchibamba.margin.domain.usecase.ObserveTodaysDrop
import com.paulchibamba.margin.domain.usecase.OpenDrop
import com.paulchibamba.margin.domain.usecase.PrepareTodaysDrop
import javax.inject.Inject

class DomainDropUseCases @Inject constructor(
    private val startup: StartupInitializer,
    private val observeTodaysDrop: ObserveTodaysDrop,
    private val prepareTodaysDrop: PrepareTodaysDrop,
    private val openDrop: OpenDrop,
    private val enterDropItem: EnterDropItem,
    private val leaveDropItem: LeaveDropItem,
    private val endDropVisit: EndDropVisit,
    private val describeDropCompletion: DescribeDropCompletion,
) : DropUseCases {

    override fun observeToday() = observeTodaysDrop()

    override suspend fun prepareToday() {
        startup.ensureImported()
        prepareTodaysDrop()
    }

    override suspend fun open() = openDrop()

    override suspend fun enter(index: Int, item: FeedItem) = enterDropItem(index, Candidate(item.post, item.source))

    override suspend fun leave(index: Int) = leaveDropItem(index)

    override suspend fun continueIntoFeed() = endDropVisit.continueIntoFeed()

    override suspend fun dismiss() = endDropVisit.dismiss()

    override suspend fun completion() = describeDropCompletion()
}
