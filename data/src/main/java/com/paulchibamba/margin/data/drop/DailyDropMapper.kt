package com.paulchibamba.margin.data.drop

import com.paulchibamba.margin.data.database.entity.DailyDropEntity
import com.paulchibamba.margin.data.repository.mapper.SourceNames
import com.paulchibamba.margin.domain.drop.DailyDrop
import com.paulchibamba.margin.domain.drop.DropItem
import com.paulchibamba.margin.domain.drop.DropSlot
import com.paulchibamba.margin.domain.model.PostId
import java.time.Instant
import java.time.LocalDate
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

object DailyDropMapper {
    private const val POST = "postId"
    private const val SLOT = "slot"
    private const val SOURCE = "source"
    private const val ENTERED_AT_STEP = "enteredAtStep"

    fun toEntity(drop: DailyDrop) = DailyDropEntity(
        date = drop.date.toString(),
        composedAt = drop.composedAt.toEpochMilli(),
        composedAtStep = drop.composedAtStep,
        headline = drop.headline,
        items = JsonArray(drop.items.map(::itemJson)).toString(),
        position = drop.position,
        completedAt = drop.completedAt?.toEpochMilli(),
        continuedIntoFeed = drop.isContinuedIntoFeed,
    )

    fun toDomain(entity: DailyDropEntity) = DailyDrop(
        date = LocalDate.parse(entity.date),
        composedAt = Instant.ofEpochMilli(entity.composedAt),
        composedAtStep = entity.composedAtStep,
        items = Json.parseToJsonElement(entity.items).jsonArray.map { item -> itemOf(item.jsonObject) },
        headline = entity.headline,
        position = entity.position,
        completedAt = entity.completedAt?.let(Instant::ofEpochMilli),
        isContinuedIntoFeed = entity.continuedIntoFeed,
    )

    private fun itemJson(item: DropItem) = buildJsonObject {
        put(POST, item.postId.value)
        put(SLOT, item.slot.name.lowercase())
        put(SOURCE, SourceNames.nameOf(item.source))
        item.enteredAtStep?.let { step -> put(ENTERED_AT_STEP, step) }
    }

    private fun itemOf(json: JsonObject) = DropItem(
        postId = PostId(json.getValue(POST).jsonPrimitive.content),
        slot = DropSlot.valueOf(json.getValue(SLOT).jsonPrimitive.content.uppercase()),
        source = SourceNames.sourceOf(json.getValue(SOURCE).jsonPrimitive.content),
        enteredAtStep = json[ENTERED_AT_STEP]?.jsonPrimitive?.int,
    )
}
