package com.paulchibamba.margin.domain.progress

import com.paulchibamba.margin.domain.model.Concept
import com.paulchibamba.margin.domain.model.ConceptId

data class AttentionFacts(val hotspots: List<ConceptHotspot>, val glancedOnly: Set<ConceptId>) {

    fun isGlancedOnly(concept: Concept): Boolean = concept.id in glancedOnly

    companion object {
        val None = AttentionFacts(hotspots = emptyList(), glancedOnly = emptySet())
    }
}
