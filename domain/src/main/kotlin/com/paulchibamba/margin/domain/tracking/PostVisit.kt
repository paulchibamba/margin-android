package com.paulchibamba.margin.domain.tracking

import com.paulchibamba.margin.domain.feed.CandidateSource
import com.paulchibamba.margin.domain.model.ConceptId
import com.paulchibamba.margin.domain.model.Format
import com.paulchibamba.margin.domain.model.PostId
import kotlin.time.Duration

data class PostVisit(
    val pageIndex: Int,
    val postId: PostId,
    val conceptId: ConceptId,
    val format: Format,
    val skinName: String,
    val source: CandidateSource,
    val step: Int,
    val words: Int,
    val expectedTime: Duration,
)
