package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.repository.Clock
import com.paulchibamba.margin.domain.repository.ProgressRepository
import javax.inject.Inject

class CountDueReviews @Inject constructor(private val progress: ProgressRepository, private val clock: Clock) {

    suspend operator fun invoke(): Int {
        val now = clock.now()
        val concepts = progress.loadFeedState()?.conceptProgress?.values.orEmpty()
        return concepts.count { concept -> concept.isDueForReview(now) }
    }
}
