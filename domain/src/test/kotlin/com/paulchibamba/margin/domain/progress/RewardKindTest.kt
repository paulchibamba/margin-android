package com.paulchibamba.margin.domain.progress

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RewardKindTest {

    @Test
    fun `there are eight kinds with stable storage keys`() {
        val keys = RewardKind.all.map(RewardKind::key)

        assertEquals(
            listOf("zoom_out", "coming_up", "callback", "comeback", "quote", "now_you_can", "milestone", "re_explain"),
            keys,
        )
        assertEquals(RewardKind.Quote, RewardKind.fromKey("quote"))
    }

    @Test
    fun `re-explain is support and every other kind is a reward`() {
        assertTrue(RewardKind.ReExplain.isSupport)
        assertFalse(RewardKind.ReExplain.isReward)
        assertTrue(RewardKind.all.minus(RewardKind.ReExplain).all(RewardKind::isReward))
    }
}
