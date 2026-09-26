package com.paulchibamba.margin.domain.feed

import com.paulchibamba.margin.domain.model.Post

data class Candidate(val post: Post, val source: CandidateSource)
