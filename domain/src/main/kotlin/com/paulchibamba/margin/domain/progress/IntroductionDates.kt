package com.paulchibamba.margin.domain.progress

import com.paulchibamba.margin.domain.model.Concept
import com.paulchibamba.margin.domain.model.Post

internal object IntroductionDates {

    fun of(context: ProgressContext): List<Introduction> = context.countedConcepts
        .filter(context::isIntroduced)
        .mapNotNull { concept -> introductionOf(concept, context) }

    private fun introductionOf(concept: Concept, context: ProgressContext): Introduction? {
        val posts = context.postsByConcept[concept.id].orEmpty()
        val firstSeen = posts.map(Post::id).mapNotNull(context.sources.firstSeen::get).minOrNull() ?: return null
        return Introduction(concept, context.dateOf(firstSeen))
    }
}
