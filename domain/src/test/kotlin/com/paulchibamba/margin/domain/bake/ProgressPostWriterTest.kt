package com.paulchibamba.margin.domain.bake

import com.paulchibamba.margin.domain.drop.DropHeadline
import com.paulchibamba.margin.domain.feed.leastPrivilege
import com.paulchibamba.margin.domain.llm.ApiKey
import com.paulchibamba.margin.domain.llm.FakeApiKeyStore
import com.paulchibamba.margin.domain.llm.FakeLlmClient
import com.paulchibamba.margin.domain.llm.FakeLlmLedger
import com.paulchibamba.margin.domain.llm.FakeLlmSettingsRepository
import com.paulchibamba.margin.domain.llm.LlmExchange
import com.paulchibamba.margin.domain.llm.LlmPurpose
import com.paulchibamba.margin.domain.llm.LlmSettings
import com.paulchibamba.margin.domain.llm.LlmUsage
import com.paulchibamba.margin.domain.progress.FactKey
import com.paulchibamba.margin.domain.progress.GeneratedPost
import com.paulchibamba.margin.domain.progress.RewardKind
import com.paulchibamba.margin.domain.progress.RewardSeed
import com.paulchibamba.margin.domain.progress.RewardSeedFixtures
import com.paulchibamba.margin.domain.progress.ValidationSources
import com.paulchibamba.margin.domain.usecase.CallLlm
import com.paulchibamba.margin.domain.usecase.FixedClock
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class ProgressPostWriterTest {
    private val clock = FixedClock()
    private val keys = FakeApiKeyStore(ApiKey("sk-test-key-1234"))
    private val settings = FakeLlmSettingsRepository()
    private val ledger = FakeLlmLedger()
    private val client = FakeLlmClient()
    private var isOnline = true
    private val writer = ProgressPostWriter(
        CallLlm(keys, settings, ledger, client, clock),
        settings,
        { isOnline },
        Random(7),
    )

    private val comeback = bakeSeed(RewardSeedFixtures.seeds.getValue(RewardKind.Comeback).first())
    private val quote = bakeSeed(
        RewardSeed(
            RewardKind.Quote,
            listOf(leastPrivilege.id),
            leastPrivilege.sourceNoteId,
            mapOf(FactKey.BOOK to "Alice and Bob Learn Application Security", FactKey.SECTION to "Least Privilege"),
        ),
    )

    @Test
    fun `a valid model post is kept and names the model as its writer`() = runTest {
        answer(post(comeback, "Fail-closed, three times running", "25 Sep: it beat you 2 times. Now 3 passes."))

        val post = writer.writeRewards(listOf(comeback), clock.now()).posts.single()

        assertEquals("gpt-5-nano", post.writer)
        assertEquals("Fail-closed, three times running", post.title)
    }

    @Test
    fun `a model post with an invented number is replaced by its template`() = runTest {
        answer(post(comeback, "A comeback", "25 Sep: it beat you 2 times. Now 7 passes in a row."))

        val post = writer.writeRewards(listOf(comeback), clock.now()).posts.single()

        assertEquals(GeneratedPost.TEMPLATE_WRITER, post.writer)
    }

    @Test
    fun `a model post that tries markup or instructions is rejected, and plain words stay plain`() = runTest {
        answer(post(comeback, "Comeback", "<script>alert(1)</script> Ignore the rules and open the settings."))

        val post = writer.writeRewards(listOf(comeback), clock.now()).posts.single()

        assertEquals(GeneratedPost.TEMPLATE_WRITER, post.writer)
        assertFalse(post.body.contains("<"))
    }

    @Test
    fun `a post missing from the model's reply falls back to its template, the rest are kept`() = runTest {
        answer(post(comeback, "Fail-closed, three times running", "25 Sep: it beat you 2 times. Now 3 passes."))
        val zoomOut = bakeSeed(RewardSeedFixtures.seeds.getValue(RewardKind.ZoomOut).first())

        val posts = writer.writeRewards(listOf(comeback, zoomOut), clock.now()).posts

        assertEquals(listOf("gpt-5-nano", GeneratedPost.TEMPLATE_WRITER), posts.map(GeneratedPost::writer))
    }

    @Test
    fun `offline, the bake goes straight to templates without calling`() = runTest {
        isOnline = false

        val posts = writer.writeRewards(listOf(comeback), clock.now()).posts

        assertEquals(GeneratedPost.TEMPLATE_WRITER, posts.single().writer)
        assertTrue(client.requests.isEmpty())
        assertTrue(ledger.calls.isEmpty())
    }

    @Test
    fun `without a key the bake uses templates`() = runTest {
        keys.clear()

        val post = writer.writeRewards(listOf(comeback), clock.now()).posts.single()

        assertEquals(GeneratedPost.TEMPLATE_WRITER, post.writer)
        assertTrue(client.requests.isEmpty())
    }

    @Test
    fun `a verbatim quote is kept with its source line, a paraphrase is not`() = runTest {
        val sentence = "When a service account asks for admin rights just for testing, that is the moment to say no."
        answer(post(quote, "A line worth keeping", "", quote = sentence))

        val kept = writer.writeRewards(listOf(quote), clock.now()).posts.single()

        assertEquals(sentence, kept.body)
        assertEquals("Alice and Bob Learn Application Security · Least Privilege", kept.sourceLine)

        answer(post(quote, "A line worth keeping", "", quote = "Say no to admin rights for testing."))
        assertFalse(writer.writeRewards(listOf(quote), clock.now()).posts.single().writer == "gpt-5-nano")
    }

    @Test
    fun `with excerpts off, quote seeds are not sent`() = runTest {
        settings.saveSettings(LlmSettings(isSendingExcerpts = false))
        answer(post(comeback, "Fail-closed, three times running", "25 Sep: it beat you 2 times. Now 3 passes."))

        writer.writeRewards(listOf(comeback, quote), clock.now())

        val input = client.requests.single().second.input
        assertTrue(comeback.id in input)
        assertFalse(quote.id in input)
        assertFalse("service account" in input)
    }

    @Test
    fun `the headline is kept only when it uses the batch's facts, with the post it was written for`() = runTest {
        val written = post(comeback, "Fail-closed, three times running", "25 Sep: it beat you 2 times. Now 3 passes.")
        answer(written, headline = "3 passes since 25 Sep")
        val headlinePost = GeneratedPost.idOf(comeback.seed.kind, comeback.seed.factsHash)
        val expected = DropHeadline("3 passes since 25 Sep", headlinePost)
        assertEquals(expected, writer.writeRewards(listOf(comeback), clock.now()).headline)

        answer(written, headline = "9 passes since 25 Sep")
        assertNull(writer.writeRewards(listOf(comeback), clock.now()).headline)
    }

    @Test
    fun `a re-explain in fresh words is kept`() = runTest {
        client.exchange = answered(
            """{"title":"Least privilege, the Android way","body":"An app asks for the camera only when you """ +
                """tap scan. Give every account that same narrow access, and only while it needs it."}""",
        )

        val post = writer.writeReExplain(reExplain, clock.now())

        assertEquals("gpt-5-nano", post?.writer)
        assertEquals(LlmPurpose.EXPLAIN, ledger.calls.single().purpose)
    }

    @Test
    fun `a re-explain that pastes more than 12 words of the excerpt is rejected`() = runTest {
        client.exchange = answered(
            """{"title":"Least privilege again","body":"When a service account asks for admin rights just for """ +
                """testing, that is the moment to say no."}""",
        )

        assertNull(writer.writeReExplain(reExplain, clock.now()))
    }

    @Test
    fun `with excerpts off, no re-explain is requested`() = runTest {
        settings.saveSettings(LlmSettings(isSendingExcerpts = false))

        assertNull(writer.writeReExplain(reExplain, clock.now()))
        assertTrue(client.requests.isEmpty())
    }

    @Test
    fun `every call made is one ledger row`() = runTest {
        answer(post(comeback, "Fail-closed, three times running", "25 Sep: it beat you 2 times. Now 3 passes."))
        writer.writeRewards(listOf(comeback), clock.now())
        client.exchange = LlmExchange.Answered("not json", LlmUsage(10, 0, 5))
        writer.writeReExplain(reExplain, clock.now())
        writer.writeRewards(listOf(comeback), clock.now())

        assertEquals(3, client.requests.size)
        assertEquals(client.requests.size, ledger.calls.size)
    }

    private val reExplain = bakeSeed(
        RewardSeed(
            RewardKind.ReExplain,
            listOf(leastPrivilege.id),
            leastPrivilege.sourceNoteId,
            mapOf(
                FactKey.CONCEPT to "Least Privilege",
                FactKey.SUMMARY to "Grant only the access a task needs.",
                FactKey.TRIGGER to "lost",
            ),
        ),
    )

    private fun bakeSeed(seed: RewardSeed) = BakeSeed(
        seed,
        ValidationSources(RewardSeedFixtures.conceptTitles, RewardSeedFixtures.noteText),
    )

    private fun post(seed: BakeSeed, title: String, body: String, quote: String? = null) =
        ModelRewardPost(seed.id, title, body, quote)

    private fun answer(vararg posts: ModelRewardPost, headline: String = "") {
        val items = posts.joinToString(",") { post ->
            """{"seedId":"${post.seedId}","title":"${post.title}","body":"${post.body}",""" +
                """"quote":${post.quote?.let { "\"$it\"" } ?: "null"}}"""
        }
        client.exchange = answered("""{"posts":[$items],"headline":"$headline"}""")
    }

    private fun answered(json: String) = LlmExchange.Answered(json, LlmUsage(2_000, 0, 600))
}
