package com.paulchibamba.margin.domain.model

import com.paulchibamba.margin.domain.progress.RewardKind

data class Post(
    val id: PostId,
    val conceptId: ConceptId,
    val bookSlug: BookSlug,
    val content: PostContent,
) {
    val format: Format
        get() = content.format

    val role: PostRole
        get() = format.role

    val rewardKind: RewardKind?
        get() = (content as? PostContent.Progress)?.kind

    val affinityKey: AffinityKey
        get() = rewardKind?.let { kind -> AffinityKey.OfRewardKind(kind.key) } ?: AffinityKey.OfFormat(format)
}
