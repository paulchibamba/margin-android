package com.paulchibamba.margin.domain.progress

class RewardValidator {

    fun rejectionsOf(draft: RewardDraft, seed: RewardSeed, sources: ValidationSources): Set<Rejection> = buildSet {
        if (draft.title.length > MAX_TITLE) add(Rejection.TITLE_TOO_LONG)
        if (draft.body.length > MAX_BODY) add(Rejection.BODY_TOO_LONG)
        if (BannedPatterns.isBanned(draft, seed.kind)) add(Rejection.BANNED_PATTERN)
        val checkedText = factCheckedTextOf(draft, seed.kind)
        if (!FactsOnlyCheck.hasOnlyFactNumbers(checkedText, seed.facts)) add(Rejection.NUMBER_NOT_IN_FACTS)
        if (!FactsOnlyCheck.hasOnlyFactTitles(checkedText, seed.facts, sources.conceptTitles)) {
            add(Rejection.TITLE_NOT_IN_FACTS)
        }
        if (isMisquoted(draft, seed, sources)) add(Rejection.QUOTE_NOT_VERBATIM)
        if (isCopiedReExplain(draft, seed, sources)) add(Rejection.COPIED_FROM_EXCERPT)
        if (MARKUP.containsMatchIn("${draft.title}\n${draft.body}")) add(Rejection.MARKUP)
    }

    fun isValid(draft: RewardDraft, seed: RewardSeed, sources: ValidationSources): Boolean =
        rejectionsOf(draft, seed, sources).isEmpty()

    private fun factCheckedTextOf(draft: RewardDraft, kind: RewardKind): String =
        if (kind == RewardKind.Quote) draft.title else "${draft.title}\n${draft.body}"

    private fun isMisquoted(draft: RewardDraft, seed: RewardSeed, sources: ValidationSources): Boolean =
        seed.kind == RewardKind.Quote && !isVerbatim(draft.body, sources.noteText)

    private fun isVerbatim(quote: String, noteText: String?): Boolean =
        noteText != null && quote.isNotBlank() && normalized(quote) in normalized(noteText)

    private fun isCopiedReExplain(draft: RewardDraft, seed: RewardSeed, sources: ValidationSources): Boolean {
        val excerpt = sources.noteText ?: return false
        val isModelReExplain = seed.kind == RewardKind.ReExplain && sources.isModelWritten
        return isModelReExplain && ExcerptCopyCheck.copiesTooMuch(draft.body, excerpt)
    }

    private fun normalized(text: String): String = text.replace(WHITESPACE, " ").trim()

    companion object {
        const val MAX_TITLE = 60
        const val MAX_BODY = 280
        private val WHITESPACE = Regex("""\s+""")
        private val MARKUP = Regex("""</?[A-Za-z!]|\]\(|`|&[a-z]+;|\*\*""")
    }
}
