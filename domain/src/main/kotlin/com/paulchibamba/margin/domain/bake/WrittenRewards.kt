package com.paulchibamba.margin.domain.bake

import com.paulchibamba.margin.domain.drop.DropHeadline
import com.paulchibamba.margin.domain.progress.GeneratedPost

data class WrittenRewards(val posts: List<GeneratedPost>, val headline: DropHeadline?)
