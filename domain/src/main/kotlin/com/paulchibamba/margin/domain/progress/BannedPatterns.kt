package com.paulchibamba.margin.domain.progress

internal object BannedPatterns {
    private val CHEERLEADING = Regex(
        """great job|well done|amazing|awesome|you got this|keep it up|don['’]t worry|""" +
            """it['’]s ok(ay)? to struggle""",
        RegexOption.IGNORE_CASE,
    )
    private val CLAIMS_ABOUT_OTHERS = listOf(
        Regex("""\d+ ?% of""", RegexOption.IGNORE_CASE),
        Regex("""\bmost (people|developers|engineers)\b""", RegexOption.IGNORE_CASE),
    )
    private val GUILT_QUESTION = Regex("""\b(are|did|have|do|why|will) you\b[^.?]*\?""", RegexOption.IGNORE_CASE)
    private val EMOJI = Regex("""[\x{1F000}-\x{1FAFF}\x{2600}-\x{27BF}\x{2B00}-\x{2BFF}\x{FE0F}]""")

    fun isBanned(draft: RewardDraft, kind: RewardKind): Boolean {
        val text = "${draft.title}\n${draft.body}"
        return '!' in text ||
            EMOJI.containsMatchIn(draft.body) ||
            CHEERLEADING.containsMatchIn(text) ||
            CLAIMS_ABOUT_OTHERS.any { pattern -> pattern.containsMatchIn(text) } ||
            GUILT_QUESTION.containsMatchIn(text) ||
            (kind == RewardKind.ReExplain && '?' in text)
    }
}
