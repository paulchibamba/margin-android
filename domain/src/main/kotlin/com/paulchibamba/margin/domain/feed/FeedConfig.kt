package com.paulchibamba.margin.domain.feed

import com.paulchibamba.margin.domain.feed.CandidateSource.ANGLE
import com.paulchibamba.margin.domain.feed.CandidateSource.REWARD
import com.paulchibamba.margin.domain.feed.CandidateSource.NEW
import com.paulchibamba.margin.domain.feed.CandidateSource.PREVIEW
import com.paulchibamba.margin.domain.feed.CandidateSource.RESURFACE
import com.paulchibamba.margin.domain.feed.CandidateSource.REVIEW

data class FeedConfig(
    val sourceWeight: Map<CandidateSource, Double> =
        mapOf(NEW to 1.0, REVIEW to 1.3, ANGLE to 0.7, RESURFACE to 0.3, REWARD to 1.6, PREVIEW to 0.9),
    val formatWeight: Double = 0.8,
    val noveltyWeight: Double = 0.4,
    val noveltyWindow: Int = 4,
    val urgencyWeight: Double = 3.0,
    val urgencyCap: Double = 0.5,
    val bookWaitWeight: Double = 0.05,
    val bookWaitCap: Double = 0.6,
    val priorityWeight: Double = 3.0,
    val priorityCap: Double = 0.6,
    val reteachWeight: Double = 0.8,
    val proveItWeight: Double = 0.4,
    val jitterMax: Double = 0.05,
    val epsilon: Double = 0.15,
    val desiredRetention: Double = 0.9,
    val untestedRecallGap: Double = 0.05,
    val rewardEvery: IntRange = 6..10,
    val previewEvery: Int = 6,
    val recentlyShownWindow: Int = 30,
    val noRepeatConcept: Int = 3,
    val maxTestsInRow: Int = 2,
    val resurfaceAfter: Int = 15,
)
