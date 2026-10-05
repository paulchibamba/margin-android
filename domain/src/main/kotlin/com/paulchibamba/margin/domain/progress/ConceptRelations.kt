package com.paulchibamba.margin.domain.progress

import com.paulchibamba.margin.domain.model.Concept
import com.paulchibamba.margin.domain.model.ConceptId

class ConceptRelations(concepts: List<Concept>) {
    private val keywordsByConcept: Map<ConceptId, Set<String>> =
        concepts.associate { concept -> concept.id to keywordsOf(concept.title) }
    private val titlesPerKeyword: Map<String, Int> =
        keywordsByConcept.values.flatten().groupingBy { keyword -> keyword }.eachCount()

    fun areRelated(first: Concept, second: Concept): Boolean =
        first.id != second.id && (isSameSection(first, second) || sharesRareKeyword(first, second))

    fun relatedTo(concept: Concept, candidates: List<Concept>): List<Concept> =
        candidates.filter { candidate -> areRelated(concept, candidate) }

    private fun isSameSection(first: Concept, second: Concept): Boolean =
        first.bookSlug == second.bookSlug && first.section.isNotBlank() && first.section == second.section

    private fun sharesRareKeyword(first: Concept, second: Concept): Boolean {
        val shared = keywordsOf(first) intersect keywordsOf(second)
        return shared.any { keyword -> (titlesPerKeyword[keyword] ?: 0) <= MAX_TITLES_PER_KEYWORD }
    }

    private fun keywordsOf(concept: Concept): Set<String> =
        keywordsByConcept[concept.id] ?: keywordsOf(concept.title)

    private fun keywordsOf(title: String): Set<String> = WORD.findAll(title.lowercase())
        .map { match -> singularOf(match.value) }
        .filter { word -> word.length >= MIN_KEYWORD_LENGTH && word !in GENERIC_WORDS }
        .toSet()

    private fun singularOf(word: String): String =
        if (word.endsWith("s") && !word.endsWith("ss")) word.dropLast(1) else word

    private companion object {
        const val MIN_KEYWORD_LENGTH = 4
        const val MAX_TITLES_PER_KEYWORD = 4
        val WORD = Regex("[a-z][a-z0-9]+")
        val GENERIC_WORDS = setOf(
            "about", "against", "always", "base", "based", "build", "control", "cross", "data", "first", "from",
            "into", "management", "model", "modern", "never", "practice", "real", "rule", "secure", "security",
            "side", "step", "system", "than", "that", "their", "them", "then", "this", "tool", "what", "when",
            "where", "which", "with", "your",
        )
    }
}
