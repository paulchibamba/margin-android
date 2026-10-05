package com.paulchibamba.margin.domain.progress

import kotlin.random.Random

class TemplateWriter(private val random: Random, private val validator: RewardValidator = RewardValidator()) {

    fun write(seed: RewardSeed, sources: ValidationSources): RewardDraft? = when (seed.kind) {
        RewardKind.Quote -> quoteFor(seed, sources)
        else -> RewardTemplates.of(seed.kind)
            .shuffled(random)
            .firstNotNullOfOrNull { template -> template.fill(seed.facts)?.takeIf { isValid(it, seed, sources) } }
    }

    private fun quoteFor(seed: RewardSeed, sources: ValidationSources): RewardDraft? {
        val quote = sources.noteText?.let { text -> QuotePicker.pick(text, random) } ?: return null
        val sourceLine = RewardTemplate(RewardTemplates.QUOTE_SOURCE, quote).fill(seed.facts)?.title
        return RewardTemplates.quoteTitles
            .shuffled(random)
            .mapNotNull { title -> RewardTemplate(title, quote).fill(seed.facts) }
            .map { draft -> draft.copy(sourceLine = sourceLine) }
            .firstOrNull { draft -> isValid(draft, seed, sources) }
    }

    private fun isValid(draft: RewardDraft, seed: RewardSeed, sources: ValidationSources): Boolean =
        validator.isValid(draft, seed, sources)
}
