package com.paulchibamba.margin.domain.usecase

import com.paulchibamba.margin.domain.actions.ActionLogEntry
import com.paulchibamba.margin.domain.actions.PostAction
import com.paulchibamba.margin.domain.bake.BakeTrigger
import com.paulchibamba.margin.domain.bake.FakeBakeStateStore
import com.paulchibamba.margin.domain.bake.ProgressPostWriter
import com.paulchibamba.margin.domain.feed.FeedState
import com.paulchibamba.margin.domain.feed.cia
import com.paulchibamba.margin.domain.feed.leastPrivilege
import com.paulchibamba.margin.domain.feed.mcqOf
import com.paulchibamba.margin.domain.feed.tipOf
import com.paulchibamba.margin.domain.feed.withIntroduced
import com.paulchibamba.margin.domain.llm.ApiKey
import com.paulchibamba.margin.domain.llm.FakeApiKeyStore
import com.paulchibamba.margin.domain.llm.FakeLlmClient
import com.paulchibamba.margin.domain.llm.FakeLlmLedger
import com.paulchibamba.margin.domain.llm.FakeLlmSettingsRepository
import com.paulchibamba.margin.domain.llm.LlmExchange
import com.paulchibamba.margin.domain.llm.LlmUsage
import com.paulchibamba.margin.domain.progress.GeneratedPost
import com.paulchibamba.margin.domain.progress.RewardKind
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class BakeProgressPostsTest {
    private val fixture = UseCaseFixture()
    private val bakeState = FakeBakeStateStore()
    private val keys = FakeApiKeyStore()
    private val client = FakeLlmClient()
    private val ledger = FakeLlmLedger()
    private var isOnline = false
    private val llmSettings = FakeLlmSettingsRepository()
    private val writer = ProgressPostWriter(
        CallLlm(keys, llmSettings, ledger, client, fixture.clock),
        llmSettings,
        { isOnline },
        Random(3),
    )
    private val bake = BakeProgressPosts(
        BuildProgressFacts(fixture.content, fixture.progress, fixture.settings, fixture.eventLog, fixture.clock),
        fixture.generatedPosts,
        fixture.content,
        bakeState,
        writer,
        fixture.clock,
        Random(3),
    )

    @Test
    fun `offline, template posts are still baked`() = runTest {
        introduceChapterOneToday()

        val report = bake(BakeTrigger.SCHEDULED)

        assertTrue(report.rewards > 0)
        assertTrue(fixture.generatedPosts.posts.all { it.writer == GeneratedPost.TEMPLATE_WRITER })
        assertEquals(fixture.clock.now(), bakeState.state.lastBakeAt)
        assertTrue(client.requests.isEmpty())
    }

    @Test
    fun `running the bake twice on the same facts inserts nothing new`() = runTest {
        introduceChapterOneToday()
        bake(BakeTrigger.SCHEDULED)
        val baked = fixture.generatedPosts.posts.toList()

        val again = bake(BakeTrigger.MANUAL)

        assertEquals(0, again.rewards)
        assertTrue(again.isNothingNew)
        assertEquals(baked, fixture.generatedPosts.posts)
    }

    @Test
    fun `a session that brought no new facts bakes no rewards`() = runTest {
        fixture.progress.feedState.value = FeedState(rewardAtStep = 9).withIntroduced(cia, leastPrivilege)

        val report = bake(BakeTrigger.SESSION_END)

        assertEquals(0, report.rewards)
        assertTrue(fixture.generatedPosts.posts.isEmpty())
    }

    @Test
    fun `a re-explain bake makes no reward posts`() = runTest {
        introduceChapterOneToday()

        val report = bake(BakeTrigger.RE_EXPLAIN)

        assertEquals(0, report.rewards)
        assertTrue(fixture.generatedPosts.posts.none { it.kind.isReward })
    }

    @Test
    fun `with a key, a struggle gets a re-explain written by the model`() = runTest {
        isOnline = true
        keys.save(ApiKey("sk-test-key-1234"))
        fixture.progress.feedState.value = FeedState(rewardAtStep = 9).withIntroduced(cia)
        val lost = ActionLogEntry(fixture.clock.instant.minusSeconds(60), 3, mcqOf(cia).id, cia.id, PostAction.LOST)
        fixture.progress.actions.value = listOf(lost)
        client.exchange = LlmExchange.Answered(
            """{"title":"Another way in","body":"Think of an app that only asks for the camera when you tap scan."}""",
            LlmUsage(500, 0, 200),
        )

        val report = bake(BakeTrigger.RE_EXPLAIN)

        assertEquals(1, report.reExplains)
        val reExplain = assertNotNull(fixture.generatedPosts.posts.singleOrNull { it.kind == RewardKind.ReExplain })
        assertEquals("gpt-5-nano", reExplain.writer)
        assertEquals(1, ledger.calls.size)
    }

    private fun introduceChapterOneToday() {
        fixture.progress.feedState.value = FeedState(rewardAtStep = 9).withIntroduced(cia, leastPrivilege)
        fixture.progress.firstSeen[tipOf(cia).id] = fixture.clock.instant.minusSeconds(3_600)
        fixture.progress.firstSeen[tipOf(leastPrivilege).id] = fixture.clock.instant.minusSeconds(600)
    }
}
