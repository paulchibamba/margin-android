package com.paulchibamba.margin.feature.feed

import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.usecase.CaughtUp

data class FeedUiState(
    val pages: List<FeedPage> = emptyList(),
    val currentIndex: Int = 0,
    val streak: Int = 0,
    val sheetPageIndex: Int? = null,
    val nudge: FeedNudge? = null,
    val caughtUp: CaughtUp? = null,
    val coverPaths: Map<BookSlug, String> = emptyMap(),
) {
    val isCaughtUp: Boolean
        get() = caughtUp != null

    val caughtUpCoverPath: String?
        get() = caughtUp?.nextNote?.let { note -> coverPaths[note.outline.bookSlug] }

    val isLoading: Boolean
        get() = pages.isEmpty() && !isCaughtUp

    val pageCount: Int
        get() = pages.size + if (isCaughtUp) 1 else 0

    val sheetPage: FeedPage?
        get() = sheetPageIndex?.let(pages::getOrNull)

    fun isCaughtUpPage(index: Int): Boolean = isCaughtUp && index == pages.size

    fun nudgeOn(index: Int): FeedNudge? = nudge?.takeIf { it.pageIndex == index }
}
