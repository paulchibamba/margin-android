package com.paulchibamba.margin.domain.usecase

import kotlin.time.Duration

data class CaughtUp(val nextNote: NextNote?, val nextReviewIn: Duration?)
