package com.paulchibamba.margin.feature.drop

import com.paulchibamba.margin.domain.drop.DropEvidence

object DropLabels {

    fun progress(dropIndex: Int, size: Int): String = "Drop · ${dropIndex + 1} of $size"

    fun evidence(evidence: DropEvidence): String = when (evidence) {
        is DropEvidence.Remembered -> "${ideas(evidence.count)} moved to remembered."
        is DropEvidence.Passed -> "${countOf(evidence.count, "review")} passed today."
        is DropEvidence.Introduced -> "${countOf(evidence.count, "new idea")} met today."
        is DropEvidence.PostsSeen -> "${countOf(evidence.count, "post")} today."
    }

    private fun ideas(count: Int): String = countOf(count, "idea")

    private fun countOf(count: Int, noun: String): String = if (count == 1) "1 $noun" else "$count ${noun}s"
}
