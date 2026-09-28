package com.paulchibamba.margin.domain.rewards

import com.paulchibamba.margin.domain.model.Book

data class EarnedBadge(val badge: Badge, val book: Book, val completion: BookCompletion)
