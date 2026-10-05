package com.paulchibamba.margin.data.tracking

import com.paulchibamba.margin.domain.feed.CandidateSource
import com.paulchibamba.margin.domain.memory.Rating
import com.paulchibamba.margin.domain.tracking.Event
import com.paulchibamba.margin.domain.tracking.EventFixtures
import com.paulchibamba.margin.domain.tracking.EventType
import com.paulchibamba.margin.domain.tracking.LoggedEvent
import com.paulchibamba.margin.domain.tracking.SessionId
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.seconds
import org.junit.Test

class EventParserTest {

    private fun roundTrip(event: Event): Event? =
        EventParser.parse(event.type.key, EventProps.subjectOf(event), EventProps.of(event).toString())

    @Test
    fun `every event type reads back as the event that was stored`() {
        val stored = EventFixtures.all() + Event.ImageZoom(postId = EventFixtures.post, noteId = null)

        assertEquals(stored, stored.map(::roundTrip))
    }

    @Test
    fun `an impression stored with the old delight source reads back as reward`() {
        val impression = EventFixtures.forType(EventType.POST_IMPRESSION) as Event.PostImpression
        val props = EventProps.of(impression).toString().replace("\"new\"", "\"delight\"")

        val parsed = EventParser.parse(impression.type.key, impression.postId.value, props)

        assertEquals(impression.copy(source = CandidateSource.REWARD), parsed)
    }

    @Test
    fun `a self-graded answer keeps its missing correctness`() {
        val answer = Event.PostAnswer(EventFixtures.post, null, 4.seconds, Rating.AGAIN, openedSourceFirst = true)

        assertEquals(answer, roundTrip(answer))
    }

    @Test
    fun `an unknown type, broken props or a missing subject are skipped`() {
        val exposureProps = EventProps.of(EventFixtures.forType(EventType.POST_EXPOSURE)).toString()

        assertNull(EventParser.parse("drop_event", null, "{}"))
        assertNull(EventParser.parse("post_exposure", "p", """{"activeMs":"soon"}"""))
        assertNull(EventParser.parse("post_exposure", null, exposureProps))
    }

    @Test
    fun `a stored row maps back to a logged event with its time and session`() {
        val event = EventFixtures.forType(EventType.POST_EXPOSURE)
        val logged = LoggedEvent(Instant.ofEpochMilli(1_000), SessionId("s"), event)

        assertEquals(logged, EventMapper.toLogged(EventMapper.toEntity(logged)))
    }
}
