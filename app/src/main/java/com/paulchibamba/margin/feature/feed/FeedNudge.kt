package com.paulchibamba.margin.feature.feed

import com.paulchibamba.margin.domain.actions.Nudge
import com.paulchibamba.margin.domain.usecase.PostContext
import kotlin.time.Duration

data class FeedNudge(val pageIndex: Int, val kind: FeedNudgeKind, val title: String, val detail: String) {

    companion object {
        fun of(pageIndex: Int, kind: Nudge, conceptTitle: String): FeedNudge = when (kind) {
            Nudge.ANOTHER_ANGLE_COMING -> FeedNudge(
                pageIndex,
                FeedNudgeKind.ANOTHER_ANGLE_COMING,
                title = "Got it. A different angle is next.",
                detail = "No quiz on $conceptTitle until it clicks.",
            )
            Nudge.TEST_COMING_SOON -> FeedNudge(
                pageIndex,
                FeedNudgeKind.TEST_COMING_SOON,
                title = "Nice. A quick check is coming up.",
                detail = "No more explanations of $conceptTitle for now.",
            )
        }

        fun seeAgain(pageIndex: Int, interval: Duration, context: PostContext): FeedNudge = FeedNudge(
            pageIndex,
            FeedNudgeKind.SEE_AGAIN,
            title = "You'll see this again in ${approximateIntervalLabel(interval)}",
            detail = "${context.bookTitle} · Ch ${context.chapterNumber} · " +
                "${context.completion.introduced}/${context.completion.total}",
        )
    }
}
