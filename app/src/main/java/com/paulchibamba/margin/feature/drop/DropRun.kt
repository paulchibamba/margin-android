package com.paulchibamba.margin.feature.drop

import com.paulchibamba.margin.domain.drop.DropCompletion

data class DropRun(val size: Int, val completion: DropCompletion? = null)
