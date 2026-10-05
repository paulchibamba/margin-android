package com.paulchibamba.margin.domain.feed

import com.paulchibamba.margin.domain.model.Concept
import com.paulchibamba.margin.domain.model.Post

object ReteachSupport {

    fun pendingFor(concept: Concept, library: LearningLibrary, state: FeedState): Post? =
        library.reExplainsOf(concept).firstOrNull { post -> !state.hasSeen(post.id) }
            ?: bookWordsForStruggle(concept, library, state)

    private fun bookWordsForStruggle(concept: Concept, library: LearningLibrary, state: FeedState): Post? {
        if (!library.isStruggling(concept)) return null
        return library.sourcePostFor(concept)?.takeUnless { source -> state.hasSeen(source.id) }
    }
}
