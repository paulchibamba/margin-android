package com.paulchibamba.margin.domain.actions

import com.paulchibamba.margin.domain.feed.Confidence
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PostViewStateTest {

    private fun choosing(vararg actions: PostAction): PostViewState =
        actions.fold(PostViewState()) { view, action -> view.afterChoosing(action) }

    @Test
    fun `Got it and Lost are mutually exclusive in one view`() {
        val got = choosing(PostAction.GOT)
        val lost = choosing(PostAction.LOST)

        assertFalse(got.canChoose(PostAction.LOST))
        assertFalse(lost.canChoose(PostAction.GOT))
        assertEquals(Confidence.GOT, choosing(PostAction.GOT, PostAction.LOST).confidence)
    }

    @Test
    fun `Save and Less count once per view, Read any number of times`() {
        val view = choosing(PostAction.SAVE, PostAction.LESS, PostAction.READ)

        assertFalse(view.canChoose(PostAction.SAVE))
        assertFalse(view.canChoose(PostAction.LESS))
        assertTrue(view.canChoose(PostAction.READ))
    }

    @Test
    fun `any action except Less counts as engaged`() {
        assertFalse(choosing(PostAction.LESS).isEngagedByAction)
        assertTrue(choosing(PostAction.LESS).isMarkedLess)
        listOf(PostAction.GOT, PostAction.LOST, PostAction.READ, PostAction.SAVE).forEach { action ->
            assertTrue(choosing(action).isEngagedByAction)
        }
    }

    @Test
    fun `a fresh view has no confidence and is not engaged`() {
        assertEquals(null, PostViewState().confidence)
        assertFalse(PostViewState().isEngagedByAction)
    }
}
