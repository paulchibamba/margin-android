package com.paulchibamba.margin.feature.feed

import com.paulchibamba.margin.domain.actions.Nudge

data class FeedNudge(val pageIndex: Int, val kind: Nudge, val title: String, val detail: String) {

    companion object {
        fun of(pageIndex: Int, kind: Nudge, conceptTitle: String): FeedNudge = when (kind) {
            Nudge.ANOTHER_ANGLE_COMING -> FeedNudge(
                pageIndex,
                kind,
                title = "Got it. A different angle is next.",
                detail = "No quiz on $conceptTitle until it clicks.",
            )
            Nudge.TEST_COMING_SOON -> FeedNudge(
                pageIndex,
                kind,
                title = "Nice. A quick check is coming up.",
                detail = "No more explanations of $conceptTitle for now.",
            )
        }
    }
}
