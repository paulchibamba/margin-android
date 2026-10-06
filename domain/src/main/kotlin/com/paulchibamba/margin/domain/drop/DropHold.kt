package com.paulchibamba.margin.domain.drop

import com.paulchibamba.margin.domain.feed.CandidateSource
import com.paulchibamba.margin.domain.feed.FeedState
import com.paulchibamba.margin.domain.feed.HeldBack
import com.paulchibamba.margin.domain.feed.LearningLibrary

object DropHold {

    fun of(drop: DailyDrop?, state: FeedState, library: LearningLibrary): HeldBack {
        val remaining = drop?.remainingIndices(state)?.map(drop.items::get) ?: return HeldBack.Nothing
        val reviewPosts = remaining.filter { item -> item.source == CandidateSource.REVIEW }.map(DropItem::postId)
        val reviewedConcepts = library.conceptsInBookOrder
            .filter { concept -> library.testPostsOf(concept).any { test -> test.id in reviewPosts } }
            .map { concept -> concept.id }
        return HeldBack(remaining.map(DropItem::postId).toSet(), reviewedConcepts.toSet())
    }
}
