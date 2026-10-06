package com.paulchibamba.margin.domain.bake

import com.paulchibamba.margin.domain.drop.DropHeadline
import com.paulchibamba.margin.domain.llm.LlmModel
import com.paulchibamba.margin.domain.llm.LlmReply
import com.paulchibamba.margin.domain.progress.GeneratedPost
import com.paulchibamba.margin.domain.progress.RewardDraft
import com.paulchibamba.margin.domain.progress.RewardKind
import com.paulchibamba.margin.domain.progress.RewardTemplate
import com.paulchibamba.margin.domain.progress.RewardTemplates
import com.paulchibamba.margin.domain.progress.RewardValidator
import com.paulchibamba.margin.domain.progress.TemplateWriter
import com.paulchibamba.margin.domain.repository.Connectivity
import com.paulchibamba.margin.domain.repository.LlmSettingsRepository
import com.paulchibamba.margin.domain.usecase.CallLlm
import java.time.Instant
import javax.inject.Inject
import kotlin.random.Random

class ProgressPostWriter @Inject constructor(
    private val callLlm: CallLlm,
    private val llmSettings: LlmSettingsRepository,
    private val connectivity: Connectivity,
    private val random: Random,
) {
    private val validator = RewardValidator()

    suspend fun writeRewards(seeds: List<BakeSeed>, now: Instant): WrittenRewards {
        val model = modelBatchFor(seeds)
        val posts = seeds.mapNotNull { seed -> modelPostFor(seed, model, now) ?: templatePostFor(seed, now) }
        return WrittenRewards(posts, model?.let { headlineOf(it, seeds) })
    }

    private fun headlineOf(model: ModelBatch, seeds: List<BakeSeed>): DropHeadline? {
        val text = HeadlineCheck.validOrNull(model.batch.headline, seeds.map(BakeSeed::seed)) ?: return null
        val last = model.lastSeed.seed
        return DropHeadline(text, GeneratedPost.idOf(last.kind, last.factsHash))
    }

    suspend fun writeReExplain(seed: BakeSeed, now: Instant): GeneratedPost? {
        if (!connectivity.isOnline() || !llmSettings.settings().isSendingExcerpts) return null
        val reply = callLlm(ReExplainRequest.of(seed)) as? LlmReply.Answered ?: return null
        val written = ModelReplies.reExplain(reply.json) ?: return null
        return validPost(seed, RewardDraft(written.title.trim(), written.body.trim()), reply.model, now)
    }

    private suspend fun modelBatchFor(seeds: List<BakeSeed>): ModelBatch? {
        val sendable = sendableAmong(seeds)
        if (sendable.isEmpty() || !connectivity.isOnline()) return null
        val reply = callLlm(RewardBatchRequest.of(sendable)) as? LlmReply.Answered ?: return null
        val batch = ModelReplies.rewardBatch(reply.json) ?: return null
        return ModelBatch(batch, reply.model, sendable.last())
    }

    private suspend fun sendableAmong(seeds: List<BakeSeed>): List<BakeSeed> {
        val isSendingExcerpts = llmSettings.settings().isSendingExcerpts
        return seeds.filter { seed ->
            seed.seed.kind != RewardKind.Quote || (isSendingExcerpts && seed.sources.noteText != null)
        }
    }

    private fun modelPostFor(seed: BakeSeed, model: ModelBatch?, now: Instant): GeneratedPost? {
        val written = model?.batch?.posts?.firstOrNull { post -> post.seedId == seed.id } ?: return null
        return validPost(seed, draftOf(seed, written), model.model, now)
    }

    private fun draftOf(seed: BakeSeed, written: ModelRewardPost): RewardDraft {
        if (seed.seed.kind != RewardKind.Quote) return RewardDraft(written.title.trim(), written.body.trim())
        val quote = written.quote.orEmpty().trim()
        val sourceLine = RewardTemplate(RewardTemplates.QUOTE_SOURCE, quote).fill(seed.seed.facts)?.title
        return RewardDraft(written.title.trim(), quote, sourceLine)
    }

    private fun validPost(seed: BakeSeed, draft: RewardDraft, model: LlmModel, now: Instant): GeneratedPost? {
        val sources = seed.sources.copy(isModelWritten = true)
        if (!validator.isValid(draft, seed.seed, sources)) return null
        return GeneratedPost.from(seed.seed, draft, model.id, now)
    }

    private fun templatePostFor(seed: BakeSeed, now: Instant): GeneratedPost? =
        TemplateWriter(random, validator).write(seed.seed, seed.sources)
            ?.let { draft -> GeneratedPost.from(seed.seed, draft, GeneratedPost.TEMPLATE_WRITER, now) }

    private class ModelBatch(val batch: ModelRewardBatch, val model: LlmModel, val lastSeed: BakeSeed)
}
