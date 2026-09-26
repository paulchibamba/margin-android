package com.paulchibamba.margin.domain.actions

import com.paulchibamba.margin.domain.feed.Confidence

data class PostViewState(val chosenActions: Set<PostAction> = emptySet()) {

    val confidence: Confidence?
        get() = when {
            PostAction.GOT in chosenActions -> Confidence.GOT
            PostAction.LOST in chosenActions -> Confidence.LOST
            else -> null
        }

    val isMarkedLess: Boolean
        get() = PostAction.LESS in chosenActions

    val isEngagedByAction: Boolean
        get() = chosenActions.any { action -> action != PostAction.LESS }

    fun canChoose(action: PostAction): Boolean = when (action) {
        PostAction.READ -> true
        PostAction.GOT, PostAction.LOST -> confidence == null
        PostAction.SAVE, PostAction.LESS -> action !in chosenActions
    }

    fun afterChoosing(action: PostAction): PostViewState =
        if (canChoose(action)) copy(chosenActions = chosenActions + action) else this
}
