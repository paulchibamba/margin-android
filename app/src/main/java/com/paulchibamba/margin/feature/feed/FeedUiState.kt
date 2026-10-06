package com.paulchibamba.margin.feature.feed

import com.paulchibamba.margin.domain.drop.DropStatus
import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.usecase.CaughtUp
import com.paulchibamba.margin.feature.drop.DropRun

data class FeedUiState(
    val pages: List<FeedPage> = emptyList(),
    val currentIndex: Int = 0,
    val streak: Int = 0,
    val sheetPageIndex: Int? = null,
    val nudge: FeedNudge? = null,
    val caughtUp: CaughtUp? = null,
    val coverPaths: Map<BookSlug, String> = emptyMap(),
    val dropStatus: DropStatus? = null,
    val drop: DropRun? = null,
) {
    val dropPillLabel: String?
        get() = dropStatus?.takeUnless { it.isFinished }?.let { drop -> "Today's drop · ${drop.done}/${drop.size}" }

    val isCaughtUp: Boolean
        get() = caughtUp != null

    val caughtUpCoverPath: String?
        get() = caughtUp?.nextNote?.let { note -> coverPaths[note.outline.bookSlug] }

    val isLoading: Boolean
        get() = pages.isEmpty() && !isCaughtUp && drop == null

    val pageCount: Int
        get() = pages.size + (if (isCaughtUp) 1 else 0) + (if (drop != null) DROP_END_PAGES else 0)

    val sheetPage: FeedPage?
        get() = sheetPageIndex?.let(pages::getOrNull)

    fun isCaughtUpPage(index: Int): Boolean = isCaughtUp && index == pages.size

    fun nudgeOn(index: Int): FeedNudge? = nudge?.takeIf { it.pageIndex == index }

    fun isDropCompletionPage(index: Int): Boolean = drop != null && index == pages.size

    fun isDropContinuePage(index: Int): Boolean = drop != null && index == pages.size + 1

    private companion object {
        const val DROP_END_PAGES = 2
    }
}
