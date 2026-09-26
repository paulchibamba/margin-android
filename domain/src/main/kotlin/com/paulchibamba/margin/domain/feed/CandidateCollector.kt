package com.paulchibamba.margin.domain.feed

import com.paulchibamba.margin.domain.feed.source.AngleProvider
import com.paulchibamba.margin.domain.feed.source.DelightProvider
import com.paulchibamba.margin.domain.feed.source.NewConceptProvider
import com.paulchibamba.margin.domain.feed.source.PreviewProvider
import com.paulchibamba.margin.domain.feed.source.ResurfaceProvider
import com.paulchibamba.margin.domain.feed.source.ReviewProvider
import java.time.Instant

class CandidateCollector(private val providers: List<CandidateProvider>) {

    fun collect(library: LearningLibrary, state: FeedState, now: Instant): List<Candidate> =
        providers.flatMap { provider -> provider.candidates(library, state, now) }

    companion object {
        fun from(config: FeedConfig) = CandidateCollector(
            listOf(
                NewConceptProvider(),
                PreviewProvider(),
                DelightProvider(),
                ReviewProvider(),
                AngleProvider(),
                ResurfaceProvider(config.resurfaceAfter),
            ),
        )
    }
}
