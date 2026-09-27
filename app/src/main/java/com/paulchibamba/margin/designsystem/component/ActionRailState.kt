package com.paulchibamba.margin.designsystem.component

import androidx.compose.runtime.Immutable
import com.paulchibamba.margin.domain.actions.PostAction
import com.paulchibamba.margin.domain.actions.PostViewState
import com.paulchibamba.margin.domain.feed.Confidence

@Immutable
data class ActionRailState(
    val confidence: Confidence? = null,
    val isSaved: Boolean = false,
    val isMarkedLess: Boolean = false,
    val disabledActions: Set<PostAction> = emptySet(),
) {
    fun isActive(action: PostAction): Boolean = when (action) {
        PostAction.GOT -> confidence == Confidence.GOT
        PostAction.LOST -> confidence == Confidence.LOST
        PostAction.READ -> false
        PostAction.SAVE -> isSaved
        PostAction.LESS -> isMarkedLess
    }

    fun isEnabled(action: PostAction): Boolean = action !in disabledActions

    companion object {
        fun of(view: PostViewState, unavailableActions: Set<PostAction> = emptySet()): ActionRailState {
            val chosen = ActionRailState(
                confidence = view.confidence,
                isSaved = PostAction.SAVE in view.chosenActions,
                isMarkedLess = view.isMarkedLess,
            )
            val blocked = PostAction.entries.filter { !view.canChoose(it) && !chosen.isActive(it) }
            return chosen.copy(disabledActions = unavailableActions + blocked)
        }
    }
}
