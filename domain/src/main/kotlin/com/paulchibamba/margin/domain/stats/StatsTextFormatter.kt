package com.paulchibamba.margin.domain.stats

import com.paulchibamba.margin.domain.actions.PostAction
import com.paulchibamba.margin.domain.model.NotePosition
import com.paulchibamba.margin.domain.usecase.AttentionReport
import com.paulchibamba.margin.domain.usecase.StatsReport
import java.util.Locale
import kotlin.math.roundToInt

object StatsTextFormatter {

    fun format(report: StatsReport, attention: AttentionReport? = null): String = listOfNotNull(
        summaryOf(report),
        section("Format affinity", affinityLinesOf(report)),
        attention?.let { section("Attention, last ${AttentionReport.DAYS} days", attentionLinesOf(it)) },
        section("Reviews by grade", gradeLinesOf(report)),
        section("Frontier per book", frontierLinesOf(report)),
        section("Actions by type", actionLinesOf(report)),
        section("Lost concepts", lostLinesOf(report)),
    ).joinToString(separator = "\n\n", postfix = "\n")

    private fun summaryOf(report: StatsReport): String = listOf(
        "Margin stats ${report.date}",
        "Posts seen: ${report.postsSeen}",
        "Day streak: ${report.currentStreak}",
        "Marked lost: ${report.lostConcepts.size}",
        "Review accuracy: ${accuracyOf(report)}",
    ).joinToString("\n")

    private fun accuracyOf(report: StatsReport): String {
        val accuracy = report.reviewAccuracy ?: return "no reviews yet"
        return "${percentOf(accuracy)} of ${report.reviewCount} reviews"
    }

    private fun section(title: String, lines: List<String>): String =
        (listOf(title) + lines.ifEmpty { listOf("none") }.map { line -> "  $line" }).joinToString("\n")

    private fun affinityLinesOf(report: StatsReport): List<String> =
        report.affinityByStrength.map { (format, value) -> "${format.name.lowercase()} ${decimalOf(value)}" }

    private fun attentionLinesOf(attention: AttentionReport): List<String> = listOf(
        "glance rate ${attention.glanceRate?.let(::percentOf) ?: "none"}",
        "deep reads ${attention.deepRate?.let(::percentOf) ?: "none"}",
    ) + attention.ratioByFormat.map { (format, ratio) -> "ratio ${format.name.lowercase()} ${decimalOf(ratio)}" } +
        attention.hotspots.map { (title, count) -> "re-read $count $title" } +
        attention.paceByBook.map { (book, wordsPerMinute) -> "wpm ${book.slug.value} $wordsPerMinute" } +
        attention.paceByTimeOfDay.map { (time, wordsPerMinute) -> "wpm ${time.name.lowercase()} $wordsPerMinute" }

    private fun gradeLinesOf(report: StatsReport): List<String> = StatsReport.GRADES.map { grade ->
        "${grade.name.lowercase()} ${report.countOf(grade)} (${percentOf(report.shareOf(grade))})"
    }

    private fun frontierLinesOf(report: StatsReport): List<String> =
        report.frontiers.map { (book, frontier) -> "${book.slug.value}: ${frontierOf(frontier)}" }

    private fun frontierOf(position: NotePosition?): String =
        if (position == null) "not started" else "ch ${position.chapter} note ${position.order + 1}"

    private fun actionLinesOf(report: StatsReport): List<String> =
        PostAction.entries.map { action -> "${action.name.lowercase()} ${report.countOf(action)}" }

    private fun lostLinesOf(report: StatsReport): List<String> =
        report.lostConcepts.map { concept -> "${concept.id.value} ${concept.title}" }

    private fun decimalOf(value: Double): String = String.format(Locale.ROOT, "%.2f", value)

    private fun percentOf(share: Double): String = "${(share * PERCENT).roundToInt()}%"

    private const val PERCENT = 100
}
