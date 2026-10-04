package com.paulchibamba.margin.data.tracking

import com.paulchibamba.margin.domain.tracking.EventFixtures
import com.paulchibamba.margin.domain.tracking.EventType
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import org.junit.Test

class EventPrivacyTest {
    private val tokenLike = Regex("[A-Za-z0-9_:/.,\\-]*")

    @Test
    fun `every event type has a fixture and a distinct key`() {
        assertEquals(EventType.entries.size, EventFixtures.all().map { it.type.key }.toSet().size)
    }

    @Test
    fun `no event stores anything that could be book or note text`() {
        EventFixtures.all().forEach { event ->
            val stored = listOfNotNull(EventProps.subjectOf(event)) + stringsIn(EventProps.of(event))
            stored.forEach { value ->
                assertTrue(value.length <= MAX_TOKEN && tokenLike.matches(value), "${event.type.key} stores '$value'")
            }
        }
    }

    private fun stringsIn(props: JsonObject): List<String> = props.values
        .filterIsInstance<JsonPrimitive>()
        .filter { it.isString }
        .mapNotNull { it.contentOrNull }

    private companion object {
        const val MAX_TOKEN = 64
    }
}
