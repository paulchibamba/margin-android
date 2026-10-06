package com.paulchibamba.margin.feature.debug

import com.paulchibamba.margin.domain.bake.BakeReport
import com.paulchibamba.margin.domain.bake.BakeSummary
import com.paulchibamba.margin.domain.progress.GeneratedPost
import com.paulchibamba.margin.feature.settings.progressposts.spendLabel
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object BakeSummaryLabels {
    private val bakeTime = DateTimeFormatter.ofPattern("d MMM HH:mm")

    fun reportLine(report: BakeReport): String {
        val reExplains = if (report.reExplains == 0) "" else ", ${countOf(report.reExplains, "re-explain")}"
        return when {
            report.isNothingNew && report.reExplains == 0 -> "Nothing new since the last bake"
            else -> "Baked ${countOf(report.rewards, "progress post")}$reExplains"
        }
    }

    fun linesOf(summary: BakeSummary, zone: ZoneId): List<String> = listOf(
        "Last bake: " + (summary.lastBakeAt?.atZone(zone)?.format(bakeTime) ?: "never"),
        spendLabel(summary.spentToday),
    ) + summary.recentPosts.map(::postLine)

    private fun postLine(post: GeneratedPost): String = "${post.kind.key} · ${post.writer} · ${post.title}"

    private fun countOf(count: Int, noun: String): String = if (count == 1) "1 $noun" else "$count ${noun}s"
}
