package com.paulchibamba.margin.domain.progress

import com.paulchibamba.margin.domain.model.ConceptId
import java.security.MessageDigest

object FactsHash {
    private const val LENGTH = 16

    fun of(kind: RewardKind, conceptIds: List<ConceptId>, facts: Map<String, String>): String =
        sha256(canonicalFormOf(kind, conceptIds, facts)).take(LENGTH)

    private fun canonicalFormOf(kind: RewardKind, conceptIds: List<ConceptId>, facts: Map<String, String>): String {
        val sortedFacts = facts.toSortedMap().flatMap { (key, value) -> listOf(key, value) }
        val parts = listOf(kind.key) + conceptIds.map(ConceptId::value) + sortedFacts
        return parts.joinToString(separator = "") { part -> "${part.length}:$part;" }
    }

    private fun sha256(text: String): String = MessageDigest.getInstance("SHA-256")
        .digest(text.toByteArray(Charsets.UTF_8))
        .joinToString(separator = "") { byte -> "%02x".format(byte) }
}
