package com.paulchibamba.margin.domain.rewards

import com.paulchibamba.margin.domain.model.BookSlug

data class BookCompletion(
    val bookSlug: BookSlug,
    val introduced: Int,
    val remembered: Int,
    val total: Int,
) {
    val isFullyIntroduced: Boolean
        get() = total > 0 && introduced == total

    val isFullyRemembered: Boolean
        get() = total > 0 && remembered == total
}
