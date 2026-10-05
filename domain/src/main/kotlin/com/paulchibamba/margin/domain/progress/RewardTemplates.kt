package com.paulchibamba.margin.domain.progress

import com.paulchibamba.margin.domain.progress.rules.MilestoneRule
import com.paulchibamba.margin.domain.progress.rules.NowYouCanRule

object RewardTemplates {

    fun of(kind: RewardKind): List<RewardTemplate> = when (kind) {
        RewardKind.ZoomOut -> zoomOut
        RewardKind.ComingUp -> comingUp
        RewardKind.Callback -> callback
        RewardKind.Comeback -> comeback
        RewardKind.Quote -> emptyList()
        RewardKind.NowYouCan -> nowYouCan
        RewardKind.Milestone -> milestone
        RewardKind.ReExplain -> emptyList()
    }

    val quoteTitles = listOf("From {section}", "A line from {book}", "Worth reading twice")

    const val QUOTE_SOURCE = "{book} · {section}"

    private fun Map<String, String>.has(key: String, value: String) = this[key] == value

    private fun Map<String, String>.lacks(key: String, value: String) = this[key] != value

    private val zoomOut = listOf(
        RewardTemplate(
            "Zoom out: {chapter}",
            "You now hold {introduced} of {total} ideas in {chapter}. {remaining} to go, and the next one is {next}.",
        ),
        RewardTemplate(
            "Where you are in {chapter}",
            "{introduced} ideas met, {remaining} left. So far: {concepts}.",
        ) { facts -> facts.lacks(FactKey.REMAINING, "0") },
        RewardTemplate(
            "All of {chapter}",
            "Every idea in {chapter} has now come up: {concepts}. Seen together, they are one chapter's argument.",
        ) { facts -> facts.has(FactKey.REMAINING, "0") },
        RewardTemplate(
            "Zoom out on {rereadConcept}",
            "You came back to {rereadConcept} more than once. Here is where it sits: {introduced} of {total} ideas " +
                "in {chapter} so far.",
        ),
        RewardTemplate(
            "{rereadConcept} in context",
            "{rereadConcept} is one of {introduced} ideas you have met in {chapter}: {concepts}.",
        ),
    )

    private val comingUp = listOf(
        RewardTemplate(
            "Coming up: {concept}",
            "Read on. In about {notesAway} notes, {section} brings in {concept}, and it builds on {knownConcept}.",
        ) { facts -> facts.lacks(FactKey.NOTES_AWAY, "1") },
        RewardTemplate(
            "Next: {concept}",
            "The next note, {section}, brings in {concept}. You already hold the piece it leans on: {knownConcept}.",
        ) { facts -> facts.has(FactKey.NOTES_AWAY, "1") },
        RewardTemplate(
            "Where {knownConcept} leads",
            "{knownConcept} comes back in {section}, as {concept}. Read on to see how they fit.",
        ),
    )

    private val callback = listOf(
        RewardTemplate(
            "Remember {olderConcept}",
            "{daysAgo} days ago you met {olderConcept}. Today's idea, {concept}, is its sibling.",
        ),
        RewardTemplate(
            "{concept} and {olderConcept}",
            "{olderConcept} then, {concept} today, {daysAgo} days apart. Worth holding side by side.",
        ),
        RewardTemplate(
            "A link back {daysAgo} days",
            "{concept} connects to something you met {daysAgo} days ago: {olderConcept}.",
        ),
    )

    private val comeback = listOf(
        RewardTemplate(
            "{concept}, revisited",
            "{date}: {concept} beat you {failCount}×. Since then you have got it right {passCount} times in a row.",
        ),
        RewardTemplate(
            "Comeback: {concept}",
            "On {date} this one got past you. Now it is {passCount} correct answers running.",
        ),
        RewardTemplate(
            "It used to beat you",
            "{concept} beat you {failCount}× around {date}. The last {passCount} reviews went your way.",
        ),
    )

    private val nowYouCan = listOf(
        RewardTemplate(
            "{concept}, for the long term",
            "{concept} is now in long-term memory. Next time it comes up in a code review, you will spot it.",
        ) { facts -> facts.has(FactKey.WIN, NowYouCanRule.WIN_REMEMBERED) },
        RewardTemplate(
            "Now you can: {concept}",
            "{concept} has stuck. When it comes up at work, you will be the one who knows why it matters.",
        ),
        RewardTemplate(
            "{concept} is yours",
            "You can now explain {concept} without looking it up. That is the kind of thing that stops a bug " +
                "before it ships.",
        ),
        RewardTemplate(
            "{concept}, settled",
            "{concept} used to trip you up. Now it does not, and that changes what you notice in real code.",
        ) { facts -> facts.has(FactKey.WIN, NowYouCanRule.WIN_COMEBACK) },
    )

    private val milestone = listOf(
        RewardTemplate(
            "{name}: the journey",
            "{name}: {introduced} ideas met, {remembered} remembered, {days} days.",
        ),
        RewardTemplate(
            "Every idea in {name}",
            "You have now met all {introduced} ideas in {name}.",
        ) { facts -> facts.has(FactKey.MILESTONE, MilestoneRule.KIND_INTRODUCED) },
        RewardTemplate(
            "{name}, remembered",
            "All {remembered} ideas in {name} are now in long-term memory.",
        ) { facts -> facts.has(FactKey.MILESTONE, MilestoneRule.KIND_REMEMBERED) },
        RewardTemplate(
            "Milestone: {name}",
            "{introduced} ideas met and {remembered} of them remembered in {name}.",
        ),
    )
}
