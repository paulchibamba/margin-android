package com.paulchibamba.margin.domain.memory

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.double
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File
import java.time.Instant

data class GoldenCase(val name: String, val steps: List<GoldenStep>)

data class GoldenStep(
    val reviewAt: Instant,
    val rating: Rating,
    val due: Instant,
    val state: CardState,
    val stability: Double,
    val difficulty: Double,
    val reps: Int,
    val lapses: Int,
    val scheduledDays: Int,
    val learningSteps: Int,
    val retrievabilityAtDue: Double,
)

object GoldenVectors {

    private const val PATH_PROPERTY = "margin.fsrsGoldenVectors"

    val file: File?
        get() = System.getProperty(PATH_PROPERTY)?.let(::File)?.takeIf(File::exists)

    fun load(file: File): List<GoldenCase> {
        val root = Json.parseToJsonElement(file.readText()).jsonObject
        return root.getValue("cases").jsonArray.map { it.jsonObject.toCase() }
    }

    private fun JsonObject.toCase() = GoldenCase(
        name = string("name"),
        steps = getValue("steps").jsonArray.map { it.jsonObject.toStep() },
    )

    private fun JsonObject.toStep() = GoldenStep(
        reviewAt = Instant.parse(string("review_at")),
        rating = Rating.entries.single { it.value == int("rating") },
        due = Instant.parse(string("due")),
        state = CardState.entries.single { it.value == int("state") },
        stability = double("stability"),
        difficulty = double("difficulty"),
        reps = int("reps"),
        lapses = int("lapses"),
        scheduledDays = int("scheduled_days"),
        learningSteps = int("learning_steps"),
        retrievabilityAtDue = double("retrievability_at_due"),
    )

    private fun JsonObject.string(key: String) = getValue(key).jsonPrimitive.content
    private fun JsonObject.int(key: String) = getValue(key).jsonPrimitive.int
    private fun JsonObject.double(key: String) = getValue(key).jsonPrimitive.double
}
