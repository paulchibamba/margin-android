package com.paulchibamba.margin.domain.feed.filter

import com.paulchibamba.margin.domain.feed.Candidate
import com.paulchibamba.margin.domain.feed.CandidateSource
import com.paulchibamba.margin.domain.feed.FeedState

class PreviewSpacing(private val previewEvery: Int) : FeedFilter {
    override val name = "preview spacing"

    override fun keeps(candidate: Candidate, state: FeedState): Boolean {
        if (candidate.source != CandidateSource.PREVIEW) return true
        val lastPreview = state.lastPreviewAtStep ?: return true
        return state.step - lastPreview >= previewEvery
    }
}
