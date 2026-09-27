package com.paulchibamba.margin.designsystem.component

import com.paulchibamba.margin.domain.actions.PostAction
import com.paulchibamba.margin.domain.actions.PostViewState
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ActionRailStateTest {

    @Test
    fun `a fresh post has every action enabled and none active`() {
        val state = ActionRailState.of(PostViewState())
        PostAction.entries.forEach { action ->
            assertTrue(state.isEnabled(action), "$action enabled")
            assertFalse(state.isActive(action), "$action active")
        }
    }

    @Test
    fun `choosing Got it lights it and disables Lost`() {
        val state = ActionRailState.of(PostViewState().afterChoosing(PostAction.GOT))
        assertTrue(state.isActive(PostAction.GOT))
        assertTrue(state.isEnabled(PostAction.GOT))
        assertFalse(state.isActive(PostAction.LOST))
        assertFalse(state.isEnabled(PostAction.LOST))
    }

    @Test
    fun `choosing Lost lights it and disables Got it`() {
        val state = ActionRailState.of(PostViewState().afterChoosing(PostAction.LOST))
        assertTrue(state.isActive(PostAction.LOST))
        assertFalse(state.isEnabled(PostAction.GOT))
    }

    @Test
    fun `Save and Less stay lit and enabled once chosen`() {
        val view = PostViewState().afterChoosing(PostAction.SAVE).afterChoosing(PostAction.LESS)
        val state = ActionRailState.of(view)
        listOf(PostAction.SAVE, PostAction.LESS).forEach { action ->
            assertTrue(state.isActive(action), "$action active")
            assertTrue(state.isEnabled(action), "$action enabled")
        }
    }

    @Test
    fun `unavailable actions are disabled`() {
        val state = ActionRailState.of(PostViewState(), unavailableActions = setOf(PostAction.READ))
        assertEquals(setOf(PostAction.READ), state.disabledActions)
    }
}
