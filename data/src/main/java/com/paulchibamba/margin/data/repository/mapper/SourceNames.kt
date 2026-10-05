package com.paulchibamba.margin.data.repository.mapper

import com.paulchibamba.margin.domain.feed.CandidateSource

object SourceNames {
    private const val LEGACY_DELIGHT = "delight"

    fun nameOf(source: CandidateSource): String = source.name.lowercase()

    fun sourceOf(name: String): CandidateSource =
        if (name == LEGACY_DELIGHT) CandidateSource.REWARD else CandidateSource.valueOf(name.uppercase())
}
