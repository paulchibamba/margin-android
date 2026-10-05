package com.paulchibamba.margin.data.progress

import com.paulchibamba.margin.data.database.CONCEPT
import com.paulchibamba.margin.data.database.DatabaseTest
import com.paulchibamba.margin.data.database.conceptEntity
import com.paulchibamba.margin.data.database.entity.NoteReadEntity
import com.paulchibamba.margin.data.database.packContent
import com.paulchibamba.margin.domain.model.ConceptId
import com.paulchibamba.margin.domain.model.NoteId
import com.paulchibamba.margin.domain.progress.FactKey
import com.paulchibamba.margin.domain.progress.GeneratedPost
import com.paulchibamba.margin.domain.progress.RewardDraft
import com.paulchibamba.margin.domain.progress.RewardKind
import com.paulchibamba.margin.domain.progress.RewardSeed
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.Duration
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class RoomGeneratedPostRepositoryTest : DatabaseTest() {
    private val repository by lazy { RoomGeneratedPostRepository(database) }
    private val now = Instant.parse("2026-10-05T12:00:00Z")
    private val concept = ConceptId(CONCEPT)

    private fun postOf(
        kind: RewardKind = RewardKind.Comeback,
        concepts: List<ConceptId> = listOf(concept),
        facts: Map<String, String> = mapOf(FactKey.DATE to "25 Sep"),
        note: NoteId? = null,
    ): GeneratedPost {
        val seed = RewardSeed(kind, concepts, note, facts)
        return GeneratedPost.from(seed, RewardDraft("Title", "Body", "Source"), GeneratedPost.TEMPLATE_WRITER, now)
    }

    private suspend fun storeConcepts() = database.contentDao().replaceAll(packContent())

    @Test
    fun `a post round-trips with its id built from kind and facts hash`() = runTest {
        val post = postOf()

        repository.insert(listOf(post))

        assertEquals(listOf(post), repository.all())
        assertEquals("gen/comeback/${post.factsHash}", repository.all().single().id.value)
    }

    @Test
    fun `inserting a post with an existing facts hash is skipped`() = runTest {
        val post = postOf()

        assertEquals(1, repository.insert(listOf(post)))
        assertEquals(0, repository.insert(listOf(post.copy(title = "Other words"))))
        assertEquals("Title", repository.all().single().title)
    }

    @Test
    fun `fresh posts are unexpired, unseen and not marked Less`() = runTest {
        storeConcepts()
        val shown = postOf(facts = mapOf("n" to "1"))
        val less = postOf(facts = mapOf("n" to "2"))
        val fresh = postOf(facts = mapOf("n" to "3"))
        repository.insert(listOf(shown, less, fresh))

        repository.markShown(shown.id, now)
        repository.markLess(less.id)

        assertEquals(listOf(fresh), repository.fresh(now))
        assertTrue(repository.fresh(now.plus(Duration.ofDays(7))).isEmpty())
    }

    @Test
    fun `a coming up post expires once its note is read`() = runTest {
        storeConcepts()
        val note = NoteId("alice-bob-appsec/ch01/n002")
        val comingUp = postOf(RewardKind.ComingUp, note = note)
        repository.insert(listOf(comingUp))

        assertEquals(listOf(comingUp), repository.fresh(now.plus(Duration.ofDays(30))))
        database.readingDao().markNoteRead(NoteReadEntity(note.value, readAt = 1))
        assertTrue(repository.fresh(now).isEmpty())
    }

    @Test
    fun `a post whose concept no longer exists is hidden, not deleted`() = runTest {
        storeConcepts()
        val orphan = postOf(concepts = listOf(ConceptId("gone")))
        repository.insert(listOf(orphan))

        assertTrue(repository.fresh(now).isEmpty())
        assertEquals(listOf(orphan), repository.all())
    }

    @Test
    fun `a pack import leaves generated posts alone`() = runTest {
        storeConcepts()
        repository.insert(listOf(postOf()))

        database.contentDao().replaceAll(packContent(title = "Second edition").copy(concepts = listOf(conceptEntity())))

        assertEquals(1, repository.all().size)
        assertEquals(1, repository.fresh(now).size)
    }
}
