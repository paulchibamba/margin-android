package com.paulchibamba.margin.feature.drop

import com.paulchibamba.margin.domain.drop.DropCompletion
import com.paulchibamba.margin.domain.drop.DropSession
import com.paulchibamba.margin.domain.drop.DropStatus
import com.paulchibamba.margin.domain.feed.FeedItem
import kotlinx.coroutines.flow.Flow

interface DropUseCases {
    fun observeToday(): Flow<DropStatus?>
    suspend fun prepareToday()
    suspend fun open(): DropSession?
    suspend fun enter(index: Int, item: FeedItem): FeedItem?
    suspend fun leave(index: Int): DropStatus?
    suspend fun continueIntoFeed()
    suspend fun dismiss()
    suspend fun completion(): DropCompletion
}
