package com.paulchibamba.margin.domain.rewards

import com.paulchibamba.margin.domain.model.BookSlug

data class Badge(val bookSlug: BookSlug, val kind: BadgeKind)
